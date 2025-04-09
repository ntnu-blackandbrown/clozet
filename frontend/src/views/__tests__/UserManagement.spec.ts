import { mount, flushPromises } from '@vue/test-utils'
import { nextTick } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import UserManagement from '@/views/admin/users/UserManagement.vue'
import { UserService } from '@/api/services/UserService'

// Mock the UserService methods
vi.mock('@/api/services/UserService', () => ({
  UserService: {
    getAllUsers: vi.fn(),
    updateUser: vi.fn(),
  },
}))

describe('UserManagement', () => {
  const sampleUsers = [
    {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      username: 'johndoe',
      email: 'john@example.com',
      role: 'ADMIN',
      active: true,
      createdAt: '2025-01-01T12:00:00Z',
    },
    {
      id: 2,
      firstName: 'Jane',
      lastName: 'Smith',
      username: 'janesmith',
      email: 'jane@example.com',
      role: 'ROLE_USER',
      active: false,
      createdAt: '2025-02-01T12:00:00Z',
    },
    {
      id: 3,
      firstName: 'Alice',
      lastName: 'Wonder',
      username: 'alice',
      email: 'alice@example.com',
      role: 'ROLE_USER',
      active: true,
      createdAt: '2025-03-01T12:00:00Z',
    },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('fetches users on mount and displays them in a table', async () => {
    ;(UserService.getAllUsers as any).mockResolvedValue({ data: sampleUsers })
    const wrapper = mount(UserManagement)
    await flushPromises()

    // Expect one table row per user
    const rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(sampleUsers.length)

    // Check that full names, usernames, emails, and status texts are rendered
    expect(wrapper.text()).toContain('John Doe')
    expect(wrapper.text()).toContain('johndoe')
    expect(wrapper.text()).toContain('john@example.com')
    expect(wrapper.text()).toContain('Jane Smith')
    expect(wrapper.text()).toContain('Inactive') // Jane is inactive
    expect(wrapper.html()).toContain('Active') // Active users show "Active"
  })

  it('displays an error message if fetching users fails', async () => {
    ;(UserService.getAllUsers as any).mockRejectedValue(new Error('Network error'))
    const wrapper = mount(UserManagement)
    await flushPromises()

    expect(wrapper.text()).toContain('Failed to load users')
    // Check that a retry button is shown
    const retryButton = wrapper.find('button.btn-secondary')
    expect(retryButton.exists()).toBe(true)
  })

  it('filters users based on search query', async () => {
    ;(UserService.getAllUsers as any).mockResolvedValue({ data: sampleUsers })
    const wrapper = mount(UserManagement)
    await flushPromises()

    // Set search input to "alice" (should match user with firstName "Alice")
    const searchInput = wrapper.find('input.search-input')
    await searchInput.setValue('alice')
    await nextTick()

    const rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(1)
    expect(wrapper.text()).toContain('Alice Wonder')
  })

  it('filters users based on selected role', async () => {
    ;(UserService.getAllUsers as any).mockResolvedValue({ data: sampleUsers })
    const wrapper = mount(UserManagement)
    await flushPromises()

    // Change role filter using the select element with class "role-filter"
    const roleSelect = wrapper.find<HTMLSelectElement>('select.role-filter')
    await roleSelect.setValue('ADMIN')
    await nextTick()

    const rows = wrapper.findAll('tbody tr')
    // Only one user has role ADMIN
    expect(rows.length).toBe(1)
    expect(wrapper.text()).toContain('John Doe')
    expect(wrapper.text()).not.toContain('Jane Smith')
  })

  it('resets filters correctly', async () => {
    ;(UserService.getAllUsers as any).mockResolvedValue({ data: sampleUsers })
    const wrapper = mount(UserManagement)
    await flushPromises()

    // Apply filters: search for "Jane" and set role filter to "ROLE_USER"
    const searchInput = wrapper.find<HTMLInputElement>('input.search-input')
    await searchInput.setValue('Jane')
    const roleSelect = wrapper.find<HTMLSelectElement>('select.role-filter')
    await roleSelect.setValue('ROLE_USER')
    await nextTick()

    let rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(1)
    expect(wrapper.text()).toContain('Jane Smith')

    // Click the reset filters button (button with class "btn-secondary" in filter container)
    const resetButton = wrapper.find('button.btn-secondary')
    await resetButton.trigger('click')
    await nextTick()

    rows = wrapper.findAll('tbody tr')
    // After reset, all users should be displayed
    expect(rows.length).toBe(sampleUsers.length)
  })

  it('toggles user active status', async () => {
    ;(UserService.getAllUsers as any).mockResolvedValue({ data: sampleUsers })
    ;(UserService.updateUser as any).mockResolvedValue({})
    const wrapper = mount(UserManagement)
    await flushPromises()

    // Find the row for "John Doe" (active user)
    const johnRow = wrapper.findAll('tbody tr').find((tr) => tr.text().includes('John Doe'))
    expect(johnRow).toBeDefined()

    // In the actions column, the button should initially display "Deactivate"
    const toggleButton = johnRow?.find('button.btn-small')
    expect(toggleButton?.text()).toContain('Deactivate')

    // Click the toggle button to deactivate the user
    await toggleButton?.trigger('click')
    await flushPromises()

    // Verify that updateUser was called with toggled status: active false
    expect(UserService.updateUser).toHaveBeenCalledWith(1, { active: false })
  })

  it('changes user role using the dropdown', async () => {
    ;(UserService.getAllUsers as any).mockResolvedValue({ data: sampleUsers })
    ;(UserService.updateUser as any).mockResolvedValue({})
    const wrapper = mount(UserManagement)
    await flushPromises()

    // Find the row for "Jane Smith" (current role: ROLE_USER)
    const janeRow = wrapper.findAll('tbody tr').find((tr) => tr.text().includes('Jane Smith'))
    expect(janeRow).toBeDefined()

    // Within the row, find the role select element (with class "role-select")
    const roleSelect = janeRow?.find<HTMLSelectElement>('select.role-select')
    expect(roleSelect?.exists()).toBe(true)

    // Change the value to "ADMIN"
    await roleSelect?.setValue('ADMIN')
    await flushPromises()

    // Verify that updateUser was called with the new role
    expect(UserService.updateUser).toHaveBeenCalledWith(2, { role: 'ADMIN' })
    // The select element's value should now be updated
    expect(roleSelect?.element.value).toBe('ADMIN')
  })

  it('displays an empty state when no users match filters', async () => {
    ;(UserService.getAllUsers as any).mockResolvedValue({ data: sampleUsers })
    const wrapper = mount(UserManagement)
    await flushPromises()

    // Apply a search query that does not match any user
    const searchInput = wrapper.find('input.search-input')
    await searchInput.setValue('nonexistentuser')
    await nextTick()

    // The empty state message for filtered users should be shown
    expect(wrapper.text()).toContain('No users found matching your filters')
  })

  it('displays an empty state when no users exist', async () => {
    ;(UserService.getAllUsers as any).mockResolvedValue({ data: [] })
    const wrapper = mount(UserManagement)
    await flushPromises()

    // The empty state message for no users should be shown
    expect(wrapper.text()).toContain('No users found')
  })

  it('formats the join date properly', async () => {
    ;(UserService.getAllUsers as any).mockResolvedValue({ data: sampleUsers })
    const wrapper = mount(UserManagement)
    await flushPromises()

    // Check that the join date (createdAt) is formatted using toLocaleDateString
    const formattedDate = new Date(sampleUsers[0].createdAt).toLocaleDateString()
    expect(wrapper.text()).toContain(formattedDate)
  })
})
