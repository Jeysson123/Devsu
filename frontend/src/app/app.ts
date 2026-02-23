import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Alert } from './presentation/components/alert/alert';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Alert],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('frontend');
  
  constructor() {
    console.log('APP INIT - El interceptor manejará la autenticación automáticamente');
  }
}