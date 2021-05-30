CREATE
    TABLE
        roles(
            id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
            name TEXT NOT NULL UNIQUE,
            created_at TIMESTAMP WITHOUT TIME ZONE,
            updated_at TIMESTAMP WITHOUT TIME ZONE
        );

INSERT
    INTO
        roles(name)
    VALUES('USER'),
    ('ADMIN');

CREATE
    TABLE
        users(
            id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
            email TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            name TEXT NOT NULL,
            enabled BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMP WITHOUT TIME ZONE,
            updated_at TIMESTAMP WITHOUT TIME ZONE
        );

CREATE
    TABLE
        users_roles(
            user_id INTEGER NOT NULL,
            role_id INTEGER NOT NULL,
            PRIMARY KEY(
                user_id,
                role_id
            ),
            CONSTRAINT fk_users_roles_on_users FOREIGN KEY(user_id) REFERENCES users(id),
            CONSTRAINT fk_users_roles_on_roles FOREIGN KEY(role_id) REFERENCES roles(id)
        );

CREATE
    TABLE
        verification_tokens(
            id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
            token TEXT NOT NULL,
            user_id INTEGER NOT NULL,
            expiry_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
            created_at TIMESTAMP WITHOUT TIME ZONE,
            updated_at TIMESTAMP WITHOUT TIME ZONE,
            CONSTRAINT fk_verification_tokens_on_user FOREIGN KEY(user_id) REFERENCES users(id)
        );

CREATE
    TABLE
        areas(
            id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
            name TEXT NOT NULL,
            slug TEXT NOT NULL UNIQUE,
            created_at TIMESTAMP WITHOUT TIME ZONE,
            updated_at TIMESTAMP WITHOUT TIME ZONE,
            parent_id INTEGER,
            tree_left BIGINT NOT NULL,
            tree_right BIGINT NOT NULL,
            tree_level BIGINT NOT NULL
        );

CREATE
    TABLE
        categories(
            id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
            name TEXT NOT NULL,
            slug TEXT NOT NULL UNIQUE,
            price NUMERIC(
                12,
                2
            ) NOT NULL DEFAULT 0,
            created_at TIMESTAMP WITHOUT TIME ZONE,
            updated_at TIMESTAMP WITHOUT TIME ZONE,
            parent_id INTEGER,
            tree_left BIGINT NOT NULL,
            tree_right BIGINT NOT NULL,
            tree_level BIGINT NOT NULL
        );
