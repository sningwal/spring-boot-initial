ALTER TABLE short_urls
ADD CONSTRAINT uk_short_urls_short_key UNIQUE (short_key);