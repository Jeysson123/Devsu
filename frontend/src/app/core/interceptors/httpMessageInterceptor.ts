import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { tap, catchError, throwError } from 'rxjs';
import { AlertService } from '../service/AlertService';

export const httpMessageInterceptor: HttpInterceptorFn = (req, next) => {
  const alertService = inject(AlertService);
  const isMutation = ['POST', 'PUT', 'DELETE'].includes(req.method);

  return next(req).pipe(
    tap((event) => {
      if (isMutation && event instanceof HttpResponse) {
        const body = event.body as any;
        if (body && body.data !== undefined) {
          alertService.show(body.data, body.success);
        }
      }
    }),
    catchError((err) => {
      if (isMutation) {
        const body = err?.error ?? err;
        alertService.show(body?.data || 'Error en la operación', body?.success ?? false);
      }
      return throwError(() => err);
    })
  );
};
