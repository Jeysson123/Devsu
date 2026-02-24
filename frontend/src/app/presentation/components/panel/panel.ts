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

  public searchTerm = '';
  public selectedRow: any = null;
  public showForm = false;
  public isEditing = false;
  public editingId: number | null = null;
  public formData: any = {};
  public selectOptions: any = {};
  public movementData: any = { movementType: '', account: '', amount: null, balance: null };
  public accountsList: any[] = [];

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
    },
    movements: {
      endpoint: environment.endpoints.movements,
      columns: [
        { header: 'Fecha', key: 'date' },
        { header: 'Tipo', key: 'movementType' },
        { header: 'Monto', key: 'amount' },
        { header: 'Saldo', key: 'balance' },
        { header: 'Cuenta', key: 'accountNumber' }
      ]
    },
    accounts: {
      endpoint: environment.endpoints.accounts,
      columns: [
        { header: 'Número', key: 'accountNumber' },
        { header: 'Tipo', key: 'accountType' },
        { header: 'Saldo Inicial', key: 'initialBalance' },
        { header: 'Estado', key: 'status' },
        { header: 'Cliente', key: 'clientName' }
      ],
      createFields: [
        { label: 'Número de cuenta', key: 'accountNumber', type: 'text' },
        { label: 'Tipo de cuenta', key: 'accountType', type: 'select', options: [{value: 'Ahorros', label: 'Ahorros'}, {value: 'Corriente', label: 'Corriente'}] },
        { label: 'Saldo inicial', key: 'initialBalance', type: 'number' },
        { label: 'Estado', key: 'status', type: 'checkbox' },
        { label: 'Cliente', key: 'client', type: 'select', optionsFrom: environment.endpoints.clients, optionLabel: 'name', optionValue: 'id' }
      ],
      editFields: [
        { label: 'Número de cuenta', key: 'accountNumber', type: 'text' },
        { label: 'Tipo de cuenta', key: 'accountType', type: 'select', options: [{value: 'Ahorros', label: 'Ahorros'}, {value: 'Corriente', label: 'Corriente'}] },
        { label: 'Saldo inicial', key: 'initialBalance', type: 'number' },
        { label: 'Estado', key: 'status', type: 'checkbox' },
        { label: 'Cliente', key: 'client', type: 'select', optionsFrom: environment.endpoints.clients, optionLabel: 'name', optionValue: 'id' }
      ]
    }
  };

  ngOnInit(): void {
    this.fetchData();
    if (this.screen === 'movements') {
      this.loadAccounts();
    }
  }

  private fetchData(): void {
    const screenConfig = this.config[this.screen];
    if (!screenConfig) return;

    const search = this.searchTerm.trim();
    const url = `${screenConfig.endpoint}?page=${this.currentPage}&size=${this.pageSize}${search ? '&searchTerm=' + encodeURIComponent(search) : ''}`;
    this.httpService.get<any>(url).subscribe({
      next: (res) => {
        this.data = res.data.content;
        if (this.screen === 'accounts') {
          this.data.forEach((item: any) => {
            if (item.client) {
              item.clientName = item.client.name;
              item.clientIdRef = item.client.id;
            }
          });
        }
        if (this.screen === 'movements') {
          this.data.forEach((item: any) => {
            if (item.account) {
              item.accountNumber = item.account.accountNumber;
            }
          });
        }
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
    this.loadSelectOptions();
    this.showForm = true;
  }

  openEditForm(row: any): void {
    this.isEditing = true;
    this.editingId = row.id;
    this.formData = { ...row };
    delete this.formData.id;
    delete this.formData.accounts;
    delete this.formData.movements;
    // Para accounts: cargar clientIdRef como valor del select de cliente
    if (row.clientIdRef) {
      this.formData.client = row.clientIdRef;
      delete this.formData.clientIdRef;
      delete this.formData.clientName;
    }
    this.loadSelectOptions();
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

    const payload = this.preparePayload();
    const request$ = this.isEditing
      ? this.httpService.put<any>(`${screenConfig.endpoint}/${this.editingId}`, payload)
      : this.httpService.post<any>(screenConfig.endpoint, payload);

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

  selectRow(row: any): void {
    this.selectedRow = this.selectedRow?.id === row.id ? null : row;
  }

  private loadSelectOptions(): void {
    const fields = this.formFields;
    fields.filter((f: any) => f.optionsFrom).forEach((f: any) => {
      this.httpService.get<any>(`${f.optionsFrom}?page=0&size=1000`).subscribe({
        next: (res) => {
          this.selectOptions[f.key] = (res.data.content || []).map((item: any) => ({
            value: item[f.optionValue], label: item[f.optionLabel]
          }));
          this.cdr.detectChanges();
        }
      });
    });
  }

  private preparePayload(): any {
    const data = { ...this.formData };
    delete data.clientName;
    delete data.clientIdRef;
    const fields = this.formFields;
    fields.filter((f: any) => f.optionsFrom).forEach((f: any) => {
      if (data[f.key]) {
        data[f.key] = { [f.optionValue]: Number(data[f.key]) };
      }
    });
    return data;
  }

  onSearch(): void {
    this.currentPage = 0;
    this.fetchData();
  }

  private loadAccounts(): void {
    this.httpService.get<any>(`${environment.endpoints.accounts}?page=0&size=1000`).subscribe({
      next: (res) => {
        this.accountsList = res.data.content || [];
        this.cdr.detectChanges();
      }
    });
  }

  onAccountChange(): void {
    const selected = this.accountsList.find((a: any) => a.id === this.movementData.account);
    this.movementData.balance = selected ? selected.initialBalance : null;
  }

  submitMovement(): void {
    const payload = {
      movementType: this.movementData.movementType,
      amount: Number(this.movementData.amount),
      account: { id: Number(this.movementData.account) }
    };
    this.httpService.post<any>(this.config['movements'].endpoint, payload).subscribe({
      next: () => {
        this.movementData = { movementType: '', account: '', amount: null, balance: null };
        this.fetchData();
      },
      error: (err) => console.error('[Panel movements] Error:', err)
    });
  }

  get rangeStart(): number { return this.currentPage * this.pageSize + 1; }
  get rangeEnd(): number { return Math.min(this.rangeStart + this.pageSize - 1, this.totalElements); }
  getDisplayValue(row: any, field: any): string {
    const val = row[field.key];
    if (val === null || val === undefined) return '—';
    if (field.key === 'status') return val ? 'Activo' : 'Inactivo';
    if (typeof val === 'object' && field.optionLabel) return val[field.optionLabel] ?? '—';
    return val;
  }

  get headers() { return this.config[this.screen]?.columns || []; }
  get detailFields() { return this.config[this.screen]?.editFields || []; }
  get formFields() {
    const cfg = this.config[this.screen];
    return this.isEditing ? (cfg?.editFields || []) : (cfg?.createFields || []);
  }
}
