import { Pool } from 'pg';

const pool = new Pool({
    host: 'localhost',
    user: 'postgres',
    password: 'postgress',
    database: 'db_schedule',
    port: 5432,
    max: 20,
    idleTimeoutMillis: 30000,
    connectionTimeoutMillis: 2000
});

export default pool;