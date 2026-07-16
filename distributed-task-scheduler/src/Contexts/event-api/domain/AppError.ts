export class AppError extends Error {
    constructor(
        public status: number,
        public message: string
    ) {
        super(message);

        this.name = 'App error';
    }
}