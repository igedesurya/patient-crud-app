import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPatient, NewPatient } from '../patient.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPatient for edit and NewPatientFormGroupInput for create.
 */
type PatientFormGroupInput = IPatient | PartialWithRequiredKeyOf<NewPatient>;

type PatientFormDefaults = Pick<NewPatient, 'id'>;

type PatientFormGroupContent = {
  id: FormControl<IPatient['id'] | NewPatient['id']>;
  pid: FormControl<IPatient['pid']>;
  firstName: FormControl<IPatient['firstName']>;
  lastName: FormControl<IPatient['lastName']>;
  dateOfBirth: FormControl<IPatient['dateOfBirth']>;
  gender: FormControl<IPatient['gender']>;
  address: FormControl<IPatient['address']>;
  suburb: FormControl<IPatient['suburb']>;
  state: FormControl<IPatient['state']>;
  postcode: FormControl<IPatient['postcode']>;
  phoneNumber: FormControl<IPatient['phoneNumber']>;
};

export type PatientFormGroup = FormGroup<PatientFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PatientFormService {
  createPatientFormGroup(patient: PatientFormGroupInput = { id: null }): PatientFormGroup {
    const patientRawValue = {
      ...this.getFormDefaults(),
      ...patient,
    };
    return new FormGroup<PatientFormGroupContent>({
      id: new FormControl(
        { value: patientRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      pid: new FormControl(patientRawValue.pid, {
        validators: [Validators.required],
      }),
      firstName: new FormControl(patientRawValue.firstName, {
        validators: [Validators.required],
      }),
      lastName: new FormControl(patientRawValue.lastName, {
        validators: [Validators.required],
      }),
      dateOfBirth: new FormControl(patientRawValue.dateOfBirth, {
        validators: [Validators.required],
      }),
      gender: new FormControl(patientRawValue.gender, {
        validators: [Validators.required],
      }),
      address: new FormControl(patientRawValue.address, {
        validators: [Validators.required],
      }),
      suburb: new FormControl(patientRawValue.suburb, {
        validators: [Validators.required],
      }),
      state: new FormControl(patientRawValue.state, {
        validators: [Validators.required],
      }),
      postcode: new FormControl(patientRawValue.postcode, {
        validators: [Validators.required],
      }),
      phoneNumber: new FormControl(patientRawValue.phoneNumber, {
        validators: [Validators.required],
      }),
    });
  }

  getPatient(form: PatientFormGroup): IPatient | NewPatient {
    return form.getRawValue() as IPatient | NewPatient;
  }

  resetForm(form: PatientFormGroup, patient: PatientFormGroupInput): void {
    const patientRawValue = { ...this.getFormDefaults(), ...patient };
    form.reset(
      {
        ...patientRawValue,
        id: { value: patientRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PatientFormDefaults {
    return {
      id: null,
    };
  }
}
