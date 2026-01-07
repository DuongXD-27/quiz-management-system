-- ============================================================
-- CREATE TABLE: student_quiz_result
-- PURPOSE: Store quiz completion results for students
-- CRITICAL: Prevents duplicate submissions with UNIQUE constraint
-- ============================================================

CREATE TABLE IF NOT EXISTS student_quiz_result (
    result_id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    score INTEGER NOT NULL,
    total_points INTEGER NOT NULL,
    completion_time_seconds INTEGER,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    correct_answers INTEGER,
    total_questions INTEGER,
    
    -- Foreign key constraints
    CONSTRAINT fk_student_result FOREIGN KEY (student_id) 
        REFERENCES student(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_quiz_result FOREIGN KEY (quiz_id) 
        REFERENCES quiz(quiz_id) ON DELETE CASCADE,
    
    -- CRITICAL: Prevent duplicate submissions (one student = one quiz = one result)
    CONSTRAINT uk_student_quiz_result UNIQUE (student_id, quiz_id)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_result_student ON student_quiz_result(student_id);
CREATE INDEX IF NOT EXISTS idx_result_quiz ON student_quiz_result(quiz_id);
CREATE INDEX IF NOT EXISTS idx_result_submitted ON student_quiz_result(submitted_at DESC);

-- Comments for documentation
COMMENT ON TABLE student_quiz_result IS 'Stores quiz completion results. Unique constraint prevents spam submissions.';
COMMENT ON COLUMN student_quiz_result.result_id IS 'Primary key, auto-generated';
COMMENT ON COLUMN student_quiz_result.student_id IS 'Foreign key to student table';
COMMENT ON COLUMN student_quiz_result.quiz_id IS 'Foreign key to quiz table';
COMMENT ON COLUMN student_quiz_result.score IS 'Score achieved (e.g., 80 out of 100)';
COMMENT ON COLUMN student_quiz_result.total_points IS 'Maximum possible score (e.g., 100)';
COMMENT ON COLUMN student_quiz_result.submitted_at IS 'Timestamp when quiz was submitted';

