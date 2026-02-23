import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 0) {
        return throwError(() => ({ data: 'Servidor no responde', success: false }));
      }
      if (error.error?.data !== undefined) {
        return throwError(() => error.error);
      }
      return throwError(() => ({ data: error.message, success: false }));
    })
  );
};
