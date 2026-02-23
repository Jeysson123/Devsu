import { HttpInterceptorFn } from '@angular/common/http';
import { tap } from 'rxjs';

export const httpMessageInterceptor: HttpInterceptorFn = (req, next) => {
  console.log(`[HTTP OUT]: ${req.method} ${req.url}`);
  return next(req).pipe(
    tap({
      next: (event) => { console.log(`[HTTP IN]: Success ${req.url}`); },
      error: (err) => { console.error(`[HTTP ERROR]: ${req.url}`, err); }
    })
  );
};