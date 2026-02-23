import { Component, Input, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpGenericService } from '../../../core/service/HttpGenericService';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './panel.html',
  styleUrl: './panel.scss',
})
export class Panel implements OnInit {
  @Input({ required: true }) screen!: 'clients' | 'accounts' | 'movements' | 'reports';
  
  private httpService = inject(HttpGenericService);
  private cdr = inject(ChangeDetectorRef);
  public data: any[] = [];
  public currentPage = 0;
  public totalPages = 1;
  public totalElements = 0;
  public pageSize = 5;

  private config: any = {
    clients: {
      endpoint: environment.endpoints.clients,
      columns: [
        { header: 'Nombre', key: 'name' },
        { header: 'Dirección', key: 'address' },
        { header: 'Teléfono', key: 'phone' },
        { header: 'Contraseña', key: 'password' },
        { header: 'Estado', key: 'status' }
      ]
    }
  };

  ngOnInit(): void {
    this.fetchData();
  }

  private fetchData(): void {
    const screenConfig = this.config[this.screen];
    if (!screenConfig) return;

    const url = `${screenConfig.endpoint}?page=${this.currentPage}&size=${this.pageSize}`;
    this.httpService.get<any>(url).subscribe({
      next: (res) => {
        this.data = res.data.content;
        this.currentPage = res.data.currentPage;
        this.totalPages = res.data.totalPages;
        this.totalElements = res.data.totalElements;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(`[Panel ${this.screen}] Error:`, err)
    });
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.fetchData();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.fetchData();
    }
  }

  get rangeStart(): number { return this.currentPage * this.pageSize + 1; }
  get rangeEnd(): number { return Math.min(this.rangeStart + this.pageSize - 1, this.totalElements); }
  get headers() { return this.config[this.screen]?.columns || []; }
}
