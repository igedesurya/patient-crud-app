import { Component, NgZone, OnInit, inject, signal, computed } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import {
  SortByDirective,
  SortDirective,
  SortService,
  type SortState,
  sortStateSignal
} from 'app/shared/sort';
import { FormatMediumDatePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';

import {
  ITEMS_PER_PAGE,
  PAGE_HEADER,
  TOTAL_COUNT_RESPONSE_HEADER
} from 'app/config/pagination.constants';
import {
  DEFAULT_SORT_DATA,
  ITEM_DELETED_EVENT,
  SORT
} from 'app/config/navigation.constants';
import { IPatient } from '../patient.model';
import { EntityArrayResponseType, PatientService } from '../service/patient.service';
import { PatientDeleteDialogComponent } from '../delete/patient-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'jhi-patient',
  templateUrl: './patient.component.html',
  imports: [
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    FormatMediumDatePipe,
    ItemCountComponent,
    RouterModule,
  ]
})
export class PatientComponent implements OnInit {
  subscription: Subscription | null = null;
  searchQuery = '';

  patients = signal<IPatient[]>([]);
  searchTerm = signal<string>('');
  filteredPatients = computed(() =>
    this.patients().filter(p =>
      this.searchTerm()
        .trim()
        .toLowerCase()
        .split(/\s+/)
        .every(term =>
          p.pid?.toLowerCase().includes(term) ||
          p.firstName?.toLowerCase().includes(term) ||
          p.lastName?.toLowerCase().includes(term)
        )
    )
  );

  search(): void {
    if (!this.searchQuery.trim()) {
      this.load();
      return;
    }

    this.page = 1;
    this.isLoading = true;

    this.patientService.search(this.searchQuery).subscribe({
      next: res => {
        this.patients.set(res.body ?? []);
        this.totalItems = res.body?.length ?? 0;
      },
      error: err => {
        console.error('Search failed:', err);
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }







  isLoading = false;
  sortState = sortStateSignal({});
  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  protected readonly router = inject(Router);
  protected readonly patientService = inject(PatientService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected readonly modalService = inject(NgbModal);
  protected readonly ngZone = inject(NgZone);

  trackId = (item: IPatient): number => this.patientService.getPatientIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load())
      )
      .subscribe();
  }

  delete(patient: IPatient): void {
    const modalRef = this.modalService.open(PatientDeleteDialogComponent, {
      size: 'lg',
      backdrop: 'static'
    });
    modalRef.componentInstance.patient = patient;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load())
      )
      .subscribe();
  }

  // Memuat data dari backend
  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => this.onResponseSuccess(res)
    });
  }

  // Navigasi sorting
  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page, event);
  }

  // Navigasi pagination
  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState());
  }

  // Baca query params untuk page & sort
  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(
      this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA])
    );
  }

  // Tangani response sukses
  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.patients.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IPatient[] | null): IPatient[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  // Panggilan ke backend
  protected queryBackend(): Observable<EntityArrayResponseType> {
    const pageToLoad = this.page;
    this.isLoading = true;
    const queryObject = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(this.sortState())
    };
    return this.patientService.query(queryObject).pipe(
      tap(() => (this.isLoading = false))
    );
  }

  // Update URL saat navigasi
  protected handleNavigation(page: number, sortState: SortState): void {
    const queryParamsObj = {
      page,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(sortState)
    };
    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj
      });
    });
  }
}
