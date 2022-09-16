INSERT INTO users (email, first_name, last_name, password, role, user_name )
VALUES ('admin@collectoryx.com', 'admin', 'collectoryx', '$2a$10$umwc.UQOHeU4edyHQg2aZ.3ZPp.zPYHZCbQM8zq7n5IDcQHVqELU2', 'ADMIN','admin_server');

INSERT INTO user_licenses (consumer_type, paid, state, type, license_check_machine, trial_activated)
VALUES ('user', 1, 'Activated', 'Lifetime', 1, 0);

INSERT INTO config_api_keys (api_link,logo,name,header)
VALUES ('https://api.pokemontcg.io/v2','https://pokemontcg.io/images/favicon.ico','Pokemon TCG','X-Api-Key')