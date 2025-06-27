import { Pipe, PipeTransform } from '@angular/core';

import dayjs from 'dayjs/esm';

@Pipe({
  name: 'formatMediumDate',
})
export default class FormatMediumDatePipe implements PipeTransform {
  transform(value: string | dayjs.Dayjs | null | undefined): string {
    if (!value) return '';
    const day = typeof value === 'string' ? dayjs(value) : value;
    return day.format('MMM D, YYYY'); // contoh: Jan 1, 2023
  }
}
