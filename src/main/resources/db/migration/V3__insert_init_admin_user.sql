INSERT INTO public.app_user
(id, username, "role", created_at, updated_at, password_hash)
VALUES(1, 'admin', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 '$argon2id$v=19$m=65536,t=3,p=1$S6la7zKOZSlo+2mewQOn0A$nZ5BNwYwc1USjFNzxTWNsjO9ghOPYrOVRJ5zCuk37tY');