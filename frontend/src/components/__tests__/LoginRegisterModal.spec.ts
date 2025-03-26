import { ref } from 'vue'
import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import LoginRegisterModal from '../LoginRegisterModal.vue'
// Create a shared map so that when vee-validate’s useField is called it returns the same reactive ref per field.
const fieldValues: Record<string, any> = {}
function createField(name: string) {
  if (!fieldValues[name]) {
    fieldValues[name] = ref('')
  }
  return {
    value: fieldValues[name],
    errorMessage: ref(''),
  }
}

// Stub vee-validate to simplify form behavior during tests.
vi.mock('vee-validate', () => {
  return {
    useField: (name: string) => createField(name),
    useForm: (_options: any) => {
      return {
        handleSubmit: (fn: Function) => fn,
        errors: ref({}),
        meta: ref({}),
      }
    },
  }
})

// Stub the user store so we can test register mode without calling the real API.
const handleRegisterMock = vi.fn()
vi.mock('../../stores/UserStore', () => {
  return {
    useUserStore: () => ({
      handleRegister: handleRegisterMock,
    }),
  }
})

describe('LoginRegisterModal.vue', () => {
  let wrapper: ReturnType<typeof mount>

  beforeEach(() => {
    // Reset field values.
    for (const key in fieldValues) {
      fieldValues[key].value = ''
    }
    handleRegisterMock.mockClear()
    wrapper = mount(LoginRegisterModal, {
      global: {
        stubs: {
          // Stub child components if needed.
        },
      },
    })
  })

  it('renders in login mode initially', () => {
    expect(wrapper.text()).toContain('Login')
    expect(wrapper.text()).toContain('Need an account? Register')
    // In login mode, only two inputs (identificator and password) should be visible.
    const inputs = wrapper.findAll('input')
    expect(inputs).toHaveLength(2)
  })

  it('toggles to register mode when toggle button is clicked', async () => {
    const toggleButton = wrapper.find('[data-testid="toggle-form-btn"]')
    await toggleButton.trigger('click')
    expect(wrapper.text()).toContain('Register')
    expect(wrapper.text()).toContain('Already have an account? Login')
    // In register mode, six inputs should be visible.
    const inputs = wrapper.findAll('input')
    expect(inputs).toHaveLength(6)
  })

  it('submits the login form', async () => {
    // Fill in login fields.
    const identificatorInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    await identificatorInput.setValue('userLogin')
    await passwordInput.setValue('Password1')
    const consoleLogSpy = vi.spyOn(console, 'log').mockImplementation(() => {})

    await wrapper.find('form').trigger('submit.prevent')
    expect(consoleLogSpy).toHaveBeenCalledWith('Login')
    expect(wrapper.emitted()).toHaveProperty('close')
    consoleLogSpy.mockRestore()
  })
  /*
  it('submits the register form and calls handleRegister', async () => {
    // Switch to register mode.
    const toggleButton = wrapper.find('[data-testid="toggle-form-btn"]')
    await toggleButton.trigger('click')
    const inputs = wrapper.findAll('input')
    // Order in register mode: [0] userName, [1] firstName, [2] lastName, [3] email, [4] password, [5] confirmPassword.
    await inputs[0].setValue('newUser')
    await inputs[1].setValue('First')
    await inputs[2].setValue('Last')
    await inputs[3].setValue('user@example.com')
    await inputs[4].setValue('Password1')
    await inputs[5].setValue('Password1')
    await wrapper.find('form').trigger('submit.prevent')

    expect(handleRegisterMock).toHaveBeenCalledWith(
      'newUser',
      'user@example.com',
      'Password1',
      'First',
      'Last'
    )
    expect(wrapper.emitted()).toHaveProperty('close')
  })

  it('closes the modal when clicking on the backdrop', async () => {
    // The backdrop (with class "backdrop") has a click.self listener.
    const backdrop = wrapper.find('.backdrop')
    await backdrop.trigger('click')
    expect(wrapper.emitted()).toHaveProperty('close')
  })*/
})
