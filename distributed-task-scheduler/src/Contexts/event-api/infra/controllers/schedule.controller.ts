import { Request, Response } from "express";

export class ScheduleController {
    constructor() {}

    public async run(res: Response, req: Request): Promise<void> {
        const { type, payload, schedule } = req.body;

        
        

        
    }
}