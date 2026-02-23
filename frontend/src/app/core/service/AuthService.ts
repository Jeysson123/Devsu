import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { tap, Observable, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private tokenSignal = signal<string | null>(localStorage.getItem('access_token'));

  login(): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoints.token}`, {
      clientId: environment.credentials.clientId,
      password: environment.credentials.password
    }).pipe(
      tap(res => {
        localStorage.setItem('access_token', res.data.token);
        localStorage.setItem('expires_at', (Date.now() + res.data.expiresIn * 1000).toString());
        this.tokenSignal.set(res.data.token);
      })
    );
  }

  getToken() { return this.tokenSignal(); }
  
  isTokenExpired() {
    const exp = localStorage.getItem('expires_at');
    return !exp || Date.now() > parseInt(exp);
  }

  clearSession() {
    this.tokenSignal.set(null);
    localStorage.removeItem('access_token');
    localStorage.removeItem('expires_at');
  }
}
