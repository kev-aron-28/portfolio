type Recurrence = 'once' | 'daily' | 'weekly' | 'monthly';

export interface Schedule {
    recurrence: Recurrence;
    time: string; // Time of the day, with format of "HH:mm"
    dayOfTheWeek?: number; // when weekly is choosen  0 = sunday, 6 = saturday
    dayOfTheMonth?: number; // when monthly (1-31)
    startDate?: Date; // optional, initial date
    endDate?: Date; // optional, final date
    timezone?: string; // maybe
}