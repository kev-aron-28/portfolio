create table platform_user (
    id UUID primary key,
    username varchar(50) unique not null,
    password varchar(255) not null,
    created_at timestamptz not null default now()
);

create table feature_flags(
    id UUID PRIMARY KEY,
    flag_key varchar(100) not null unique,
    description text,
    enabled boolean not null default false,
    rollout_percentage int not null default 0
        check (rollout_percentage between 0 and 100),
    created_by UUID not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint fk_feature_created_by
        foreign key (created_by)
        references platform_user(id)
);

create table feature_flag_roles (
    feature_flag_id UUID NOT NULL,
    role varchar(50) not null,
    constraint pk_feature_flag_roles 
        primary key (feature_flag_id, role),
    constraint fk_feature_role
        foreign key (feature_flag_id)
        references feature_flags(id)
        on delete cascade
);

create table feature_flag_users (
    feature_flag_id UUID NOT NULL,
    user_id UUID NOT NULL,
    constraint pk_feature_flag_users
        primary key (feature_flag_id, user_id),
    constraint fk_feature_user
        foreign key (feature_flag_id)
        references feature_flags(id)
        on delete cascade
);

create table feature_flag_audit (
    id UUID primary key,
    flag_key varchar(100) not null,
    action varchar(20) not null check (
        action in ('CREATED','UPDATED','DELETED','ENABLED','DISABLED')
    ),
    changed_by UUID,
    old_value JSONB,
    new_value JSONB,
    created_at timestamptz not null default now()
);

create index idx_feature_flag_audit_key
on feature_flag_audit(flag_key);