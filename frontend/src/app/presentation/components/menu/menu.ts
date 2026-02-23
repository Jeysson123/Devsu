// src/app/presentation/components/menu/menu.ts
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'menu',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './menu.html',
  styleUrl: './menu.scss',
})
export class Menu {
}