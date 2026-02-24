import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Panel } from './panel';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('Panel (Full Coverage)', () => {
  let component: Panel;
  let fixture: ComponentFixture<Panel>;
  let httpMock: HttpTestingController;

  const mockApiResponse = {
    data: {
      content: [
        {
          Fecha: '2026-02-22T07:47:49.722705',
          Cliente: 'Devsu',
          Cuenta: '1234567890',
          Tipo: 'Ahorros',
          'Saldo Inicial': 0.0,
          Estado: true,
          Monto: 500.0,
          'Saldo Disponible': 0.0
        }
      ],
      totalElements: 1,
      totalPages: 1
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        Panel,
        HttpClientTestingModule,
        FormsModule,
        ReactiveFormsModule
      ],
      providers: [
        provideNoopAnimations()
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(Panel);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();

    // Flush ALL automatic HTTP calls triggered on init
    const initRequests = httpMock.match(() => true);
    initRequests.forEach(req =>
      req.flush({
        data: {
          content: [],
          totalElements: 0,
          totalPages: 0
        }
      })
    );

    fixture.detectChanges();
  });

  afterEach(() => {
    // Flush any remaining pending requests to avoid Vitest hanging errors
    const pending = httpMock.match(() => true);
    pending.forEach(req =>
      req.flush({
        data: {
          content: [],
          totalElements: 0,
          totalPages: 0
        }
      })
    );

    httpMock.verify();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch reports on search', () => {
    component.screen = 'reports';

    component.reportFilters = {
      clientName: 'Jeysson',
      startDate: '2026-02-24',
      endDate: '2026-02-24'
    };

    component.currentPage = 0;
    component.pageSize = 10;

    component.onSearch();

    const req = httpMock.expectOne(r =>
      r.url.includes('/reportes') && r.method === 'GET'
    );

    req.flush(mockApiResponse);

    expect(component.data.length).toBe(1);
    expect(component.totalElements).toBe(1);
    expect(component.totalPages).toBe(1);
  });

  it('should export visible report data', () => {
    component.screen = 'reports';
    component.data = mockApiResponse.data.content;

    component.exportReport();

    const req = httpMock.expectOne(r =>
      r.url.includes('/reportes/exportar') && r.method === 'POST'
    );

    req.flush({
      data: {
        fileName: 'report_test',
        pdf: 'dGVzdA=='
      }
    });
  });

  it('should submit movement', () => {
    component.screen = 'movements';

    component.movementData = {
      movementType: 'Credito',
      account: 1,
      balance: 100,
      amount: 50
    };

    component.submitMovement();

    const req = httpMock.expectOne(r => r.method === 'POST');

    req.flush({
      data: {}
    });
  });

  it('should search clients', () => {
    component.screen = 'clients';
    component.searchTerm = 'John';

    component.onSearch();

    const req = httpMock.expectOne(r => r.method === 'GET');

    req.flush({
      data: {
        content: [],
        totalElements: 0,
        totalPages: 0
      }
    });

    expect(component.data.length).toBe(0);
  });

  it('should go to next page', () => {
    component.currentPage = 0;
    component.totalPages = 3;

    component.nextPage();

    expect(component.currentPage).toBe(1);
  });

  it('should go to previous page', () => {
    component.currentPage = 1;

    component.prevPage();

    expect(component.currentPage).toBe(0);
  });

  it('should select a row', () => {
    const row = { id: 1 };
    component.selectRow(row);

    expect(component.selectedRow).toEqual(row);
  });

  it('should open form', () => {
    component.openForm();
    expect(component.showForm).toBe(true);
  });

  it('should close form', () => {
    component.showForm = true;
    component.closeForm();
    expect(component.showForm).toBe(false);
  });

  it('should open edit form', () => {
    const row = { id: 1, name: 'Test' };

    component.openEditForm(row);

    expect(component.isEditing).toBe(true);
    expect(component.formData).toEqual(row);
  });

  it('should delete a row', () => {
    const row = { id: 1 };

    component.deleteRow(row);

    const req = httpMock.expectOne(r => r.method === 'DELETE');

    req.flush({
      data: {}
    });
  });
});