import dayjs from 'dayjs/esm';

import { IPatient, NewPatient } from './patient.model';

export const sampleWithRequiredData: IPatient = {
  id: 2514,
  pid: 'custody if who',
  firstName: 'Dovie',
  lastName: 'Shields',
  dateOfBirth: dayjs('2025-06-26'),
  gender: 'yawn faithfully',
  address: 'conversation gaseous',
  suburb: 'unto willow zowie',
  state: 'without hyena brood',
  postcode: 'tank',
  phoneNumber: 'hmph shoulder',
};

export const sampleWithPartialData: IPatient = {
  id: 12268,
  pid: 'parody',
  firstName: 'Enoch',
  lastName: 'Reilly',
  dateOfBirth: dayjs('2025-06-26'),
  gender: 'finger courageously whereas',
  address: 'sleepy mooch',
  suburb: 'upset next',
  state: 'blah however',
  postcode: 'cauliflower',
  phoneNumber: 'reboot next',
};

export const sampleWithFullData: IPatient = {
  id: 9819,
  pid: 'an',
  firstName: 'Friedrich',
  lastName: 'Miller',
  dateOfBirth: dayjs('2025-06-26'),
  gender: 'vivid fooey',
  address: 'which downchange',
  suburb: 'for materialise',
  state: 'relieve',
  postcode: 'gee following',
  phoneNumber: 'scale surprisingly clamour',
};

export const sampleWithNewData: NewPatient = {
  pid: 'kick labourer brr',
  firstName: 'Margaretta',
  lastName: 'Murazik',
  dateOfBirth: dayjs('2025-06-26'),
  gender: 'frozen alongside',
  address: 'restfully regular characterization',
  suburb: 'oh during jovially',
  state: 'gloom uniform',
  postcode: 'lovely yum',
  phoneNumber: 'blue everlasting despite',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
