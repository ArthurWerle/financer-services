CREATE TABLE IF NOT EXISTS transactions (
                              id SERIAL PRIMARY KEY,
                              category_id INTEGER REFERENCES categories(id) ON DELETE SET NULL,
                              amount DECIMAL(12, 2) NOT NULL,
                              type_id INTEGER REFERENCES types(id) ON DELETE SET NULL,
                              description TEXT,
                              date TIMESTAMP WITH TIME ZONE,
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recurring_transactions (
                                        id SERIAL PRIMARY KEY,
                                        category_id INTEGER REFERENCES categories(id) ON DELETE SET NULL,
                                        amount DECIMAL(12, 2) NOT NULL,
                                        type_id INTEGER REFERENCES types(id) ON DELETE SET NULL,
                                        description TEXT,
                                        frequency VARCHAR(50) NOT NULL,
                                        start_date DATE NOT NULL,
                                        end_date DATE,
                                        last_occurrence DATE,
                                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);