import { Routes } from '@angular/router';
import { Dashboard } from './presentation/layouts/dashboard/dashboard';

export const routes: Routes = [
  {
    path: '',
    component: Dashboard,
    children: [
      // 1. Redirección por defecto: si entras a la raíz, carga 'clients'
      { path: '', redirectTo: 'clients', pathMatch: 'full' },
      
      // 2. Definición de rutas hijas
      { 
        path: 'clientes', 
        loadComponent: () => import('./presentation/pages/clients/clients').then(m => m.Clients) 
      },
      { 
        path: 'cuentas', 
        loadComponent: () => import('./presentation/pages/accounts/accounts').then(m => m.Accounts) 
      },
      { 
        path: 'movimientos', 
        loadComponent: () => import('./presentation/pages/movements/movements').then(m => m.Movements) 
      },
      { 
        path: 'reportes', 
        loadComponent: () => import('./presentation/pages/reports/reports').then(m => m.Reports) 
      }
    ]
  },
  // 3. (Opcional) Redirigir cualquier ruta no encontrada a 'clients'
  { path: '**', redirectTo: 'clientes' }
];