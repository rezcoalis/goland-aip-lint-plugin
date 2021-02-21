## Demo

https://drive.google.com/file/d/1SbXoiKBvKbT5cw6JxA_FZfgkJ7o8AIGp/view

## Notes

* AIP spec contains rules for a lot of different platforms/languages, so users of api-linter will want to have project
  specific linter config which might look like this:

```json
[
  {
    "disabled_rules": [
      "all"
    ],
    "enabled_rules": [
      "0158"
    ],
    "included_paths": [
      "**/*"
    ]
  }
]
```

`api-linter` itself doesn't have a default config path hardcoded (only a `--config` option), so this plugin enforces
`.api-linter.json` and `.api-linter.yaml` at project root to be used as config files (first one found). 

* All `proto` files are reparsed whenever these config files are edited.

* Plugin tries to pick up `api-linter` from path.
