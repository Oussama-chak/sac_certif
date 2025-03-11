-- Disable foreign key checks to avoid errors when truncating tables with foreign keys
SET FOREIGN_KEY_CHECKS = 0;

-- Generate and execute TRUNCATE TABLE commands for all tables in the sac_certif database
SELECT concat('TRUNCATE TABLE `', table_name, '`;') 
FROM information_schema.tables 
WHERE table_schema = 'sac_certif';

-- Enable foreign key checks again
SET FOREIGN_KEY_CHECKS = 1;
