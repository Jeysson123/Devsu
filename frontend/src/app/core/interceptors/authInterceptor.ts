import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../service/AuthService';
import { switchMap, catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  if (req.url.includes('/auth')) return next(req);

  const addToken = (token: string | null) => {
    return token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  };

  const loginAndRetry = () => authService.login().pipe(
    switchMap(() => next(addToken(authService.getToken())))
  );

  if (!authService.getToken() || authService.isTokenExpired()) {
    return loginAndRetry();
  }

  return next(addToken(authService.getToken())).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.clearSession();
        return loginAndRetry();
      }
      return throwError(() => error);
    })
  );
};
