import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface AlertData {
  message: string;
  success: boolean;
}

@Injectable({ providedIn: 'root' })
export class AlertService {
  private alertSubject = new Subject<AlertData>();
  alert$ = this.alertSubject.asObservable();

  show(message: string, success: boolean): void {
    this.alertSubject.next({ message, success });
  }
}
