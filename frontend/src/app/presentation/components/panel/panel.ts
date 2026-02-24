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
  public selectOptions: any = { client: [] };
  public movementData: any = { movementType: '', account: '', amount: null, balance: null };
  public accountsList: any[] = [];

  // Filtros específicos para Reports
  // NOTE: reportFilters.clientName guarda el id (string) seleccionado en el select.
  public reportFilters = { clientName: '', startDate: '', endDate: '' };

  // Flag para activar el bloque de filtros personalizados para reports.
  // (Se dejó como propiedad añadida; no quita ninguna línea existente)
  public showReportsFilters = false;

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
    },
    reports: {
      endpoint: environment.endpoints.reports,
      columns: [
        { header: 'Fecha', key: 'date' },
        { header: 'Cliente', key: 'clientName' },
        { header: 'Cuenta', key: 'accountNumber' },
        { header: 'Tipo', key: 'accountType' },
        { header: 'Saldo Inicial', key: 'initialBalance' },
        { header: 'Monto', key: 'movementAmount' },
        { header: 'Disponible', key: 'availableBalance' }
      ]
    }
  };

  ngOnInit(): void {
    // Inicialización según pantalla
    this.initializeScreen();
  }

  private initializeScreen(): void {
    this.currentPage = 0;
    this.data = [];
    
    if (this.screen === 'movements') {
      this.loadAccounts();
    } else if (this.screen === 'reports') {
      this.loadClientsForFilter();
      // Activamos el bloque de filtros custom para reports
      this.showReportsFilters = true;
      // opcional: si quieres que por defecto el tamaño de página en reportes sea 10,
      // descomenta la siguiente línea:
      // this.pageSize = 10;
    }
    
    this.fetchData();
  }

  private loadClientsForFilter(): void {
    this.httpService.get<any>(`${environment.endpoints.clients}?page=0&size=1000`).subscribe({
      next: (res) => {
        // -> mapear value = id (string), label = name (igual que accounts)
        // guardamos id como string para evitar problemas con binding [value] -> ngModel
        this.selectOptions['client'] = (res.data.content || []).map((c: any) => ({
          value: String(c.id), 
          label: c.name
        }));
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error cargando clientes para filtros:', err)
    });
  }

  /**
   * Busca el nombre del cliente por el id guardado en reportFilters.clientName.
   * Si no encuentra nada devuelve el id recibido (por seguridad).
   */
  private getClientNameById(id: string): string {
    if (!id) return '';
    const list = this.selectOptions['client'] || [];
    const found = list.find((it: any) => String(it.value) === String(id));
    return found ? found.label : id;
  }

  public fetchData(): void {
    const screenConfig = this.config[this.screen];
    if (!screenConfig) return;

    // Para evitar doble '?' o errores en la URL construimos con base y luego añadimos parámetros.
    // Para reportes seguimos el formato:
    // `${endpoint}?clientName=...&startDate=...&endDate=...&page=${this.currentPage}&size=${this.pageSize}`
    let url = `${screenConfig.endpoint}?page=${this.currentPage}&size=${this.pageSize}`;

    if (this.screen === 'reports') {
      // Construimos desde cero para dejar la URL legible y correcta
      const parts: string[] = [];
      // clientName: el endpoint que mencionaste espera el NOMBRE (p.ej. Jeysson)
      if (this.reportFilters.clientName) {
        const clientName = this.getClientNameById(this.reportFilters.clientName);
        parts.push(`clientName=${encodeURIComponent(clientName)}`);
      }
      if (this.reportFilters.startDate) {
        parts.push(`startDate=${encodeURIComponent(this.reportFilters.startDate + 'T00:00:00')}`);
      }
      if (this.reportFilters.endDate) {
        parts.push(`endDate=${encodeURIComponent(this.reportFilters.endDate + 'T23:59:59')}`);
      }
      // page & size (aseguramos usar currentPage y pageSize)
      parts.push(`page=${this.currentPage}`);
      parts.push(`size=${this.pageSize}`);
      // Si hay partes, colocarlas luego del endpoint con '?'
      url = `${screenConfig.endpoint}?${parts.join('&')}`;
      // EJEMPLO esperado por ti:
      // /reportes?clientName=Jeysson&startDate=2026-02-24T00:00:00&endDate=2026-02-24T23:59:59&page=0&size=10
    } else {
      const search = this.searchTerm.trim();
      if (search) url += `&searchTerm=${encodeURIComponent(search)}`;
    }

    this.httpService.get<any>(url).subscribe({
      next: (res) => {
        this.processResponseData(res.data);
      },
      error: (err) => console.error(`[Panel ${this.screen}] Error al obtener datos:`, err)
    });
  }

  private processResponseData(data: any): void {
    this.data = data.content || [];
    this.currentPage = data.currentPage || 0;
    this.totalPages = data.totalPages || 1;
    this.totalElements = data.totalElements || 0;

    // Post-procesamiento para normalizar datos de visualización
    if (this.screen === 'accounts') {
      this.data.forEach(item => {
        if (item.client) {
          item.clientName = item.client.name;
          item.clientIdRef = item.client.id;
        }
      });
    } else if (this.screen === 'movements') {
      this.data.forEach(item => {
        if (item.account) item.accountNumber = item.account.accountNumber;
      });
    }
    this.cdr.detectChanges();
  }

  /**
   * EXPORTAR: construir payload con lo que se está mostrando en pantalla y llamar al endpoint exportar.
   * El backend responde con base64 'pdf' y 'json' dentro de data; aquí creamos blobs y forzamos la descarga.
   */
  public exportReport(fileName?: string): void {
    // Nombre por defecto
    const defaultName = fileName || `reporte_${this.screen}_${new Date().toISOString().slice(0,10)}`;
    // Construir array con las columnas visibles (usar headers configurados)
    const cols = this.headers || [];
    const payloadData = (this.data || []).map(row => this.buildExportRow(row, cols));

    const payload = {
      fileName: defaultName,
      data: payloadData
    };

    // Llamada al endpoint /reportes/exportar
    this.httpService.post<any>(`${environment.endpoints.reports}/exportar`, payload).subscribe({
      next: (res) => {
        const body = res.data || res; // dependiendo de la estructura que retorne tu servicio
        // Si viene pdf en base64
        if (body.pdf) {
          try {
            const pdfBlob = this.base64ToBlob(body.pdf, 'application/pdf');
            const pdfName = `${body.fileName || defaultName}.pdf`;
            this.downloadBlob(pdfBlob, pdfName);
          } catch (e) {
            console.error('Error convirtiendo pdf base64 a blob:', e);
          }
        }
        // Si viene json en base64 (como en tu ejemplo)
        if (body.json) {
          try {
            const jsonStr = atob(body.json);
            const jsonBlob = new Blob([jsonStr], { type: 'application/json' });
            const jsonName = `${body.fileName || defaultName}.json`;
            this.downloadBlob(jsonBlob, jsonName);
          } catch (e) {
            console.error('Error procesando json base64:', e);
          }
        }
      },
      error: (err) => {
        console.error('[Panel exportReport] Error al exportar:', err);
        alert('Error al exportar el reporte. Revisa la consola.');
      }
    });
  }

  /**
   * Construye una fila para exportar usando los headers (encabezados) como claves.
   * Extrae valores "representativos" de objetos anidados (client -> name, account -> accountNumber, etc).
   */
  private buildExportRow(row: any, cols: any[]): any {
    const out: any = {};
    cols.forEach((col: any) => {
      const header = col.header || col.key;
      const key = col.key;
      let val = row?.[key];

      if (val === undefined || val === null) {
        out[header] = '';
        return;
      }

      // Si es objeto, intentar propiedades comunes
      if (typeof val === 'object') {
        if (val.name) val = val.name;
        else if (val.clientName) val = val.clientName;
        else if (val.accountNumber) val = val.accountNumber;
        else if (val.label) val = val.label;
        else if (val.id !== undefined) val = val.id;
        else val = JSON.stringify(val);
      }

      // Si la columna es fecha, preferimos enviar ISO (si el valor es Date o string parseable)
      if (key.toLowerCase().includes('date') || key.toLowerCase().includes('fecha')) {
        if (val instanceof Date) {
          val = val.toISOString();
        } else if (typeof val === 'string') {
          // intentar normalizar si ya es ISO-like; si no, dejar tal cual
          const d = new Date(val);
          if (!isNaN(d.getTime())) val = d.toISOString();
        }
      }

      out[header] = val;
    });
    return out;
  }

  /**
   * Convierte base64 a Blob (útil para pdf)
   */
  private base64ToBlob(base64: string, contentType = 'application/pdf'): Blob {
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    return new Blob([byteArray], { type: contentType });
  }

  /**
   * Forzar descarga del blob en el cliente
   */
  private downloadBlob(blob: Blob, filename: string) {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    // En algunos entornos es necesario adjuntarlo al DOM para que funcione
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
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

  public openForm(): void { 
    this.isEditing = false; 
    this.editingId = null; 
    this.formData = { status: true }; 
    this.loadSelectOptions(); 
    this.showForm = true; 
  }
  
  public openEditForm(row: any): void { 
    this.isEditing = true; 
    this.editingId = row.id; 
    this.formData = { ...row }; 
    
    // Limpieza de objetos complejos para el formulario
    delete this.formData.id; 
    delete this.formData.accounts; 
    delete this.formData.movements; 
    
    if (row.clientIdRef) { 
        this.formData.client = row.clientIdRef; 
        delete this.formData.clientIdRef; 
        delete this.formData.clientName; 
    } 
    this.loadSelectOptions(); 
    this.showForm = true; 
  }

  public closeForm(): void { 
    this.showForm = false; 
    this.isEditing = false; 
    this.editingId = null; 
    this.formData = {}; 
  }

  public submitForm(): void {
    const screenConfig = this.config[this.screen];
    if (!screenConfig) return;
    
    const payload = this.preparePayload();
    const request$ = this.isEditing 
      ? this.httpService.put<any>(`${screenConfig.endpoint}/${this.editingId}`, payload) 
      : this.httpService.post<any>(screenConfig.endpoint, payload);
    
    request$.subscribe({
      next: () => { this.closeForm(); this.fetchData(); },
      error: (err) => console.error(`[Panel ${this.screen}] Error al guardar:`, err)
    });
  }

  public deleteRow(row: any): void {
    if (!confirm('¿Está seguro de eliminar este registro?')) return;
    const screenConfig = this.config[this.screen];
    this.httpService.delete<any>(`${screenConfig.endpoint}/${row.id}`).subscribe({
      next: () => this.fetchData(),
      error: (err) => console.error(`[Panel ${this.screen}] DELETE Error:`, err)
    });
  }

  public selectRow(row: any): void { this.selectedRow = this.selectedRow?.id === row.id ? null : row; }

  private loadSelectOptions(): void {
    this.formFields.filter((f: any) => f.optionsFrom).forEach((f: any) => {
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
    
    this.formFields.filter((f: any) => f.optionsFrom).forEach((f: any) => {
      if (data[f.key]) {
        data[f.key] = { [f.optionValue]: Number(data[f.key]) };
      }
    });
    return data;
  }

  public onSearch(): void { 
    // Al presionar la lupa, reiniciamos la página y pedimos datos con los filtros activos
    this.currentPage = 0; 
    this.fetchData(); 
  }

  private loadAccounts(): void {
    this.httpService.get<any>(`${environment.endpoints.accounts}?page=0&size=1000`).subscribe({
      next: (res) => { this.accountsList = res.data.content || []; this.cdr.detectChanges(); }
    });
  }

 public onAccountChange(): void {
  const selected = this.accountsList.find((a: any) => a.id === this.movementData.account);
  
  if (!selected) {
    this.movementData.balance = null;
    return;
  }

  // Tomar el último movimiento realizado
  const lastMovement = selected.movements?.length
    ? selected.movements.reduce((prev: any, curr: any) => 
        new Date(prev.date) > new Date(curr.date) ? prev : curr
      )
    : null;

  // Si hay movimientos, balance = saldo luego del último movimiento; si no, saldo inicial
  this.movementData.balance = lastMovement ? lastMovement.balance : selected.initialBalance;

  // Forzar que Angular detecte el cambio
  this.cdr.detectChanges();

}

  public submitMovement(): void {
    const payload = {
      movementType: this.movementData.movementType,
      amount: Number(this.movementData.amount),
      account: { id: Number(this.movementData.account)},
      balance: Number(this.movementData.balance),
    };

    this.httpService.post<any>(this.config['movements'].endpoint, payload).subscribe({
      next: () => { this.movementData = { movementType: '', account: '', amount: null, balance: null }; this.fetchData(); },
      error: (err) => console.error('[Panel movements] Error:', err)
    });
  }

  // Método añadido para evitar el error TS2339 y formatear valores en la plantilla
  public getDisplayValue(row: any, field: any): string {
    if (!row) return '';

    const key = field?.key ?? '';
    const raw = row[key];

    // Status booleano
    if (key === 'status') {
      return raw ? 'Activo' : 'Inactivo';
    }

    // Fechas (cualquier campo que contenga 'date' en su key)
   if (raw && typeof raw === 'string' && key.toLowerCase().includes('date')) {
    const isoPart = raw.length > 10 ? raw.substring(0, 10) : raw; // YYYY-MM-DD
    const d = new Date(isoPart);
    if (!isNaN(d.getTime())) {
      const day = String(d.getDate()).padStart(2, '0');
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const year = d.getFullYear();
      return `${day}/${month}/${year}`;
    }
  }
    // Si es nulo o indefinido
    if (raw === null || raw === undefined) return '';

    // Si es un array, unir valores útiles
    if (Array.isArray(raw)) {
      return raw.map(item => {
        if (item == null) return '';
        if (typeof item === 'object') return (item.name || item.label || item.id || JSON.stringify(item)).toString();
        return item.toString();
      }).filter(Boolean).join(', ');
    }

    // Si es un objeto, intentar mostrar una propiedad representativa
    if (typeof raw === 'object') {
      if (raw.name) return raw.name;
      if (raw.label) return raw.label;
      if (raw.accountNumber) return raw.accountNumber;
      if (raw.clientName) return raw.clientName;
      if (raw.id !== undefined) return raw.id.toString();
      // fallback: stringify (pequeño)
      try {
        return JSON.stringify(raw);
      } catch {
        return String(raw);
      }
    }

    // Por defecto devolver como string
    return String(raw);
  }

  // Getters para UI
  get rangeStart(): number { return this.currentPage * this.pageSize + 1; }
  get rangeEnd(): number { return Math.min(this.rangeStart + this.pageSize - 1, this.totalElements); }
  get headers() { return this.config[this.screen]?.columns || []; }
  get detailFields() { return this.config[this.screen]?.editFields || []; }
  get formFields() {
    const cfg = this.config[this.screen];
    return this.isEditing ? (cfg?.editFields || []) : (cfg?.createFields || []);
  }
}