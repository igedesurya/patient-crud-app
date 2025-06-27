import dayjs from 'dayjs/esm';

export interface IPatient {
  id: number;
  pid?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
  gender?: string | null;
  address?: string | null;
  suburb?: string | null;
  state?: string | null;
  postcode?: string | null;
  phoneNumber?: string | null;
}

export type NewPatient = Omit<IPatient, 'id'> & { id: null };
