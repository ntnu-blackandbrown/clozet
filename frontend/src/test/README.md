# Test Utilities

This directory contains utilities to help with testing Vue components.

## i18nMock.ts

A utility for creating mocked i18n instances for testing.

### Usage

```typescript
import { mount } from '@vue/test-utils'
import { createMockI18n } from '@/test/i18nMock'
import YourComponent from '@/components/YourComponent.vue'

describe('YourComponent', () => {
  it('renders with i18n', async () => {
    const i18n = createMockI18n()

    const wrapper = mount(YourComponent, {
      global: {
        plugins: [i18n],
      },
    })

    // Your test assertions...
  })

  it('renders with custom i18n messages', async () => {
    const i18n = createMockI18n({
      en: {
        custom: {
          message: 'My custom message',
        },
      },
    })

    const wrapper = mount(YourComponent, {
      global: {
        plugins: [i18n],
      },
    })

    // Your test assertions...
  })
})
```

## setup.ts

Contains global test setup that runs before each test:

- Sets up a fresh Pinia store
