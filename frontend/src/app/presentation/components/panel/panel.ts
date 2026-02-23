import { Component, Input, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpGenericService } from '../../../core/service/HttpGenericService';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
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

  public showForm = false;
  public isEditing = false;
  public editingId: number | null = null;
  public formData: any = {};

  private config: any = {
    clients: {
      endpoint: environment.endpoints.clients,
      columns: [
        { header: 'Nombre', key: 'name' },
        { header: 'Dirección', key: 'address' },
        { header: 'Teléfono', key: 'phone' },
        { header: 'Contraseña', key: 'password' },
        { header: 'Estado', key: 'status' }
      ],
      createFields: [
        { label: 'Nombre', key: 'name', type: 'text' },
        { label: 'Dirección', key: 'address', type: 'text' },
        { label: 'Teléfono', key: 'phone', type: 'text' },
        { label: 'Contraseña', key: 'password', type: 'password' },
        { label: 'Estado', key: 'status', type: 'checkbox' }
      ],
      editFields: [
        { label: 'Nombre', key: 'name', type: 'text' },
        { label: 'Género', key: 'gender', type: 'text' },
        { label: 'Edad', key: 'age', type: 'number' },
        { label: 'Identificación', key: 'identification', type: 'text' },
        { label: 'Dirección', key: 'address', type: 'text' },
        { label: 'Teléfono', key: 'phone', type: 'text' },
        { label: 'Client ID', key: 'clientId', type: 'text' },
        { label: 'Contraseña', key: 'password', type: 'password' },
        { label: 'Estado', key: 'status', type: 'checkbox' }
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

  openForm(): void {
    this.isEditing = false;
    this.editingId = null;
    this.formData = { status: true };
    this.showForm = true;
  }

  openEditForm(row: any): void {
    this.isEditing = true;
    this.editingId = row.id;
    this.formData = { ...row };
    delete this.formData.id;
    delete this.formData.accounts;
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.isEditing = false;
    this.editingId = null;
    this.formData = {};
  }

  submitForm(): void {
    const screenConfig = this.config[this.screen];
    if (!screenConfig) return;

    const request$ = this.isEditing
      ? this.httpService.put<any>(`${screenConfig.endpoint}/${this.editingId}`, this.formData)
      : this.httpService.post<any>(screenConfig.endpoint, this.formData);

    request$.subscribe({
      next: () => {
        this.closeForm();
        this.fetchData();
      },
      error: (err) => console.error(`[Panel ${this.screen}] Error:`, err)
    });
  }

  deleteRow(row: any): void {
    const screenConfig = this.config[this.screen];
    if (!screenConfig) return;
    this.httpService.delete<any>(`${screenConfig.endpoint}/${row.id}`).subscribe({
      next: () => this.fetchData(),
      error: (err) => console.error(`[Panel ${this.screen}] DELETE Error:`, err)
    });
  }

  get rangeStart(): number { return this.currentPage * this.pageSize + 1; }
  get rangeEnd(): number { return Math.min(this.rangeStart + this.pageSize - 1, this.totalElements); }
  get headers() { return this.config[this.screen]?.columns || []; }
  get formFields() {
    const cfg = this.config[this.screen];
    return this.isEditing ? (cfg?.editFields || []) : (cfg?.createFields || []);
  }
}
