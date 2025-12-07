import { Schedule } from "./Schedule";

export type EventType = 'HTTP' | 'EMAIL';

export interface HttpPayload {
    url: string;
    method: 'GET' | 'POST' | 'PUT' | 'DELETE';
    body?: any;
}

export interface EmailPayload {
    to: string;
    subject: string;
    body: string;
}

export interface InternalPayload {
    taskId: string;
    metadata?: Record<string, any>;
}

export type EventPayloadMap = {
    http: HttpPayload;
    email: EmailPayload;
    internal: InternalPayload;
};

export abstract class SchedulerEvent<TPayload> {
    constructor(
        protected id: number = 0,
        protected type: string,
        protected schedule?: Schedule | undefined
    ) {}

    abstract getPayload(): TPayload;

    public calculateNextRun(): Date {
        const now = new Date();

        // When theres no schedule means that is has to be inmediate
        // We substract when the worker queires with <= now() this is past due
        if(!this.schedule) {
            const immediate = new Date();
            
            immediate.setHours(immediate.getHours() - 1);
            
            return immediate;
        }

        const { recurrence, time, dayOfTheMonth, dayOfTheWeek, endDate, startDate } = this.schedule;

        const [ hours = 0, minutes = 0 ] = time.split(':').map(Number);

        switch (recurrence) {
            case 'once': {
                if(!startDate) throw new Error('startDate is need for once recurrence');
                
                return new Date(startDate);
            }
            case 'daily': {
                const next = new Date();
                
                next.setHours(hours, minutes, 0, 0);

                if(next <= now) next.setDate(now.getDate() + 1);

                return next;
            }
            case 'weekly': {
                if(dayOfTheWeek == undefined) throw new Error('dayOfTheWeek is needed for weekly recurrence');

                const next = new Date();

                next.setHours(hours, minutes, 0, 0);

                // when the diff is 0 means you are scheduling in the same day you want it to repeat
                // otherwise you are in a diff day of the weeek
                const diff = (dayOfTheWeek + 7 - next.getDay()) % 7;

                if(diff == 0 && next <= now) next.setDate(next.getDate() + 7);
                else next.setDate(next.getDate() + diff); 

                return next;
            }
            case 'monthly': {
                if(dayOfTheMonth == undefined) throw new Error('dayOfTheMonth is needed for montly recurrence');

                const next = new Date();

                next.setHours(hours, minutes,0 ,0);
                
                next.setDate(dayOfTheMonth);
                
                if(next <= now) next.setMonth(now.getMonth() + 1);
                
                return next;
            }
            default:
                throw new Error('Unsupported recurrence ' + recurrence);
        }
    }
    
    public toPrimitives(): { id: number; type: string; schedule?: Schedule | undefined; payload: TPayload } {
        return {
            id: this.id,
            type: this.type,
            schedule: this.schedule,
            payload: this.getPayload()
        }
    }
}

