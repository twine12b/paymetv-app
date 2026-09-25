import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import pluginReact from "eslint-plugin-react";

export default tseslint.config(

  // ── Ignore generated output and dependencies ──────────────────────
  { ignores: ["dist/**", "node_modules/**"] },

  // ── Plain JavaScript / JSX ────────────────────────────────────────
  // Uses the default espree parser; TypeScript syntax is NOT expected
  // in these files, so no @typescript-eslint/parser is needed here.
  {
    files: ["**/*.{js,mjs,cjs,jsx}"],
    extends: [js.configs.recommended],
    languageOptions: {
      ecmaVersion: "latest",
      sourceType: "module",
      globals: { ...globals.browser, ...globals.node },
    },
  },

  // ── TypeScript / TSX ──────────────────────────────────────────────
  // @typescript-eslint/parser replaces espree for these files, giving
  // ESLint a full TypeScript AST.  Without this parser, any TS-only
  // syntax (type aliases, interfaces, generics, enums, decorators …)
  // causes a fatal "Unexpected token" parse error and no rules run.
  {
    files: ["**/*.{ts,tsx}"],
    extends: [
      js.configs.recommended,
      ...tseslint.configs.recommended,   // array — must be spread
    ],
    languageOptions: {
      parser: tseslint.parser,           // ← the critical fix
      parserOptions: {
        ecmaVersion: "latest",
        sourceType: "module",
        ecmaFeatures: { jsx: true },     // required for .tsx files
      },
      globals: { ...globals.browser, ...globals.node },
    },
    plugins: {
      react: pluginReact,
    },
    settings: {
      // Lets eslint-plugin-react detect the installed React version
      // automatically instead of requiring a hard-coded string.
      react: { version: "detect" },
    },
    rules: {
      // React 17+ uses the automatic JSX transform (react-jsx in
      // tsconfig.json), so React does not need to be in scope.
      "react/react-in-jsx-scope": "off",

      // TypeScript types serve as prop documentation; PropTypes are
      // redundant and would conflict with TS interface definitions.
      "react/prop-types": "off",

      // Set rule for arrow Body Style to "as-needed" to allow for concise arrow functions without braces when they have a single expression.
      "arrow-body-style": ["error", "as-needed"],

      // Downgrade noisy TS rules from "error" to "warn" so the linter
      // reports them without blocking the build.  Variables/args whose
      // names start with _ are intentionally unused (common convention).
      "@typescript-eslint/no-unused-vars": [
        "warn",
        { argsIgnorePattern: "^_", varsIgnorePattern: "^_" },
      ],
      "@typescript-eslint/no-explicit-any": "warn",
    },
  },
);
