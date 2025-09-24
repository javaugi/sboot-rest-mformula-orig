-- liquibase formatted sql

-- changeset your.name:003-1
-- comment Create the performance_goals table
CREATE TABLE performance_goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    review_cycle_id BIGINT NOT NULL,
    goal_title VARCHAR(255) NOT NULL,
    goal_description TEXT,
    target_date DATE,
    status VARCHAR(50) NOT NULL,
    progress_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_performance_goals_employee_id FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE,
    CONSTRAINT fk_performance_goals_review_cycle_id FOREIGN KEY (review_cycle_id) REFERENCES review_cycle(id) ON DELETE CASCADE
);

-- changeset your.name:003-2
-- comment Create indexes for performance_goals table
CREATE INDEX idx_performance_goals_employee_id ON performance_goals(employee_id);
CREATE INDEX idx_performance_goals_review_cycle_id ON performance_goals(review_cycle_id);
CREATE INDEX idx_performance_goals_status ON performance_goals(status);