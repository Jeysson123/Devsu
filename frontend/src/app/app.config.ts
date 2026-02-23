import { ApplicationConfig, APP_INITIALIZER, inject } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { routes } from './app.routes';
import { AuthService } from './core/service/AuthService';
import { authInterceptor } from './core/interceptors/authInterceptor';
import { httpMessageInterceptor } from './core/interceptors/httpMessageInterceptor';
import { errorInterceptor } from './core/interceptors/errorInterceptor';

function initializeAuth(): () => Promise<void> {
  const authService = inject(AuthService);
  return () => {
    if (authService.getToken() && !authService.isTokenExpired()) {
      return Promise.resolve();
    }
    return firstValueFrom(authService.login()).then(() => {});
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([
      httpMessageInterceptor,
      errorInterceptor,
      authInterceptor
    ])),
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuth,
      multi: true
    }
  ]
};
