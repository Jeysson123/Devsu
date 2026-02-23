import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const msg = error.status === 0 ? 'Servidor no responde (CORS?)' : error.message;
      console.error('Interceptor de Error:', msg);
      return throwError(() => msg);
    })
  );
};