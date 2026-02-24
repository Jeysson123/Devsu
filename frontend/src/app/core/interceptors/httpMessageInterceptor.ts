import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { tap, catchError, throwError } from 'rxjs';
import { AlertService } from '../service/AlertService';
import { environment } from '../../../environments/environment';

export const httpMessageInterceptor: HttpInterceptorFn = (req, next) => {
  const alertService = inject(AlertService);
  const isMutation = ['POST', 'PUT', 'DELETE'].includes(req.method);

  return next(req).pipe(
    tap((event) => {
      if (isMutation && event instanceof HttpResponse) {
        const body = event.body as any;
        if (req.url.includes(environment.endpoints.reports) && body?.data) {
          alertService.show('Reporte generado correctamente', true);
        }
        else if (body && body.data !== undefined) {
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
