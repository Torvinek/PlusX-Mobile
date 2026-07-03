# PlusX Mobile Versioning

`versionName` is the version shown to users.

`versionCode` controls Android update order and must always increase.

Known versions:

- `1.5.3` = `9`
- `1.5.4` = `10`

Normal downgrade is not supported. Do not use `adb install -d`.

The single source of truth is `version.properties`.
