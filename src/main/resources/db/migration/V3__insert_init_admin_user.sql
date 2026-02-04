-- admin
INSERT INTO public.app_user (username, role, created_at, updated_at, password_hash)
SELECT
  'admin',
  'ADMIN',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  '$argon2id$v=19$m=65536,t=3,p=1$S6la7zKOZSlo+2mewQOn0A$nZ5BNwYwc1USjFNzxTWNsjO9ghOPYrOVRJ5zCuk37tY'
WHERE NOT EXISTS (
  SELECT 1 FROM public.app_user WHERE username = 'admin'
);

-- normal user
INSERT INTO public.app_user (username, role, created_at, updated_at, password_hash)
SELECT
  'user',
  'USER',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  '$argon2id$v=19$m=65536,t=3,p=1$S6la7zKOZSlo+2mewQOn0A$nZ5BNwYwc1USjFNzxTWNsjO9ghOPYrOVRJ5zCuk37tY'
WHERE NOT EXISTS (
  SELECT 1 FROM public.app_user WHERE username = 'user'
);

-- premium user
INSERT INTO public.app_user (username, role, created_at, updated_at, password_hash)
SELECT
  'premium',
  'PREMIUM_USER',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  '$argon2id$v=19$m=65536,t=3,p=1$S6la7zKOZSlo+2mewQOn0A$nZ5BNwYwc1USjFNzxTWNsjO9ghOPYrOVRJ5zCuk37tY'
WHERE NOT EXISTS (
  SELECT 1 FROM public.app_user WHERE username = 'premium'
);


INSERT INTO public.product ("name", description, price, quantity, deleted, deleted_at, created_at, updated_at)
SELECT 'Product 1', 'Seed product 1', 101.00, 20, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.product WHERE "name" = 'Product 1');

INSERT INTO public.product ("name", description, price, quantity, deleted, deleted_at, created_at, updated_at)
SELECT 'Product 2', 'Seed product 2', 102.00, 20, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.product WHERE "name" = 'Product 2');

INSERT INTO public.product ("name", description, price, quantity, deleted, deleted_at, created_at, updated_at)
SELECT 'Product 3', 'Seed product 3', 103.00, 20, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.product WHERE "name" = 'Product 3');

INSERT INTO public.product ("name", description, price, quantity, deleted, deleted_at, created_at, updated_at)
SELECT 'Product 4', 'Seed product 4', 104.00, 20, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.product WHERE "name" = 'Product 4');

INSERT INTO public.product ("name", description, price, quantity, deleted, deleted_at, created_at, updated_at)
SELECT 'Product 5', 'Seed product 5', 105.00, 20, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.product WHERE "name" = 'Product 5');

INSERT INTO public.product ("name", description, price, quantity, deleted, deleted_at, created_at, updated_at)
SELECT 'Product 6', 'Seed product 6', 106.00, 20, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.product WHERE "name" = 'Product 6');

INSERT INTO public.product ("name", description, price, quantity, deleted, deleted_at, created_at, updated_at)
SELECT 'Product 7', 'Seed product 7', 107.00, 20, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.product WHERE "name" = 'Product 7');

INSERT INTO public.product ("name", description, price, quantity, deleted, deleted_at, created_at, updated_at)
SELECT 'Product 8', 'Seed product 8', 108.00, 20, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.product WHERE "name" = 'Product 8');

INSERT INTO public.product ("name", description, price, quantity, deleted, deleted_at, created_at, updated_at)
SELECT 'Product 9', 'Seed product 9', 109.00, 20, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.product WHERE "name" = 'Product 9');

INSERT INTO public.product ("name", description, price, quantity, deleted, deleted_at, created_at, updated_at)
SELECT 'Product 10', 'Seed product 10', 110.00, 20, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.product WHERE "name" = 'Product 10');
