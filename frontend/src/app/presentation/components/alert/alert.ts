import { Component, inject, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlertService, AlertData } from '../../../core/service/AlertService';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (visible) {
      <div class="alert" [class.success]="alert.success" [class.error]="!alert.success">
        {{ alert.message }}
      </div>
    }
  `,
  styles: [`
    .alert {
      position: fixed;
      top: 1.5em;
      left: 50%;
      transform: translateX(-50%);
      padding: 1em 2em;
      border-radius: .75em;
      font-weight: 700;
      font-size: .95em;
      z-index: 99999;
      max-width: 500px;
      text-align: center;
      animation: slideDown .3s ease-out;
      box-shadow: 0 4px 20px rgba(0,0,0,.3);
    }

    .success {
      background-color: #d4edda;
      color: #155724;
      border: 2px solid #28a745;
    }

    .error {
      background-color: #f8d7da;
      color: #721c24;
      border: 2px solid #dc3545;
    }

    @keyframes slideDown {
      from { top: -2em; opacity: 0; }
      to   { top: 1.5em; opacity: 1; }
    }
  `]
})
export class Alert implements OnInit, OnDestroy {
  private alertService = inject(AlertService);
  private cdr = inject(ChangeDetectorRef);
  private sub!: Subscription;

  visible = false;
  alert: AlertData = { message: '', success: true };

  ngOnInit(): void {
    this.sub = this.alertService.alert$.subscribe((data) => {
      this.alert = data;
      this.visible = true;
      this.cdr.detectChanges();
      setTimeout(() => { this.visible = false; this.cdr.detectChanges(); }, 3000);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
