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

  // Si no hay token o expiró en cliente, renovar antes de enviar
  if (!authService.getToken() || authService.isTokenExpired()) {
    return loginAndRetry();
  }

  // Enviar con token actual, si el backend rechaza con 401, renovar y reintentar
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
