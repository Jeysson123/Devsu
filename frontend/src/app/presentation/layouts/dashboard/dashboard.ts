import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
// 1. Importa el componente hijo
import { Menu } from '../../components/menu/menu'; 

@Component({
  selector: 'app-dashboard',
  standalone: true,
  // 2. Agrégalo a los imports
  imports: [RouterModule, Menu], 
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard {
}