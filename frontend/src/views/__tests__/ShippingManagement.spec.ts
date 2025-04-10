import { mount, flushPromises } from '@vue/test-utils'
import { nextTick } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import ShippingManagement from '@/views/admin/shipping/ShippingManagement.vue'
import { ShippingService } from '@/api/services/ShippingService'

// Mock the ShippingService methods
vi.mock('@/api/services/ShippingService', () => ({
  ShippingService: {
    getAllShippingOptions: vi.fn(),
    createShippingOption: vi.fn(),
    updateShippingOption: vi.fn(),
    deleteShippingOption: vi.fn(),
  },
}))

describe('ShippingManagement', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // Restore any mocked window methods
    vi.spyOn(window, 'confirm').mockRestore()
  })

  it('fetches shipping options on mount and displays them', async () => {
    const sampleOptions = [
      {
        id: 1,
        name: 'Standard Shipping',
        description: 'Delivers in 3-5 days',
        estimatedDays: 4,
        price: 49.99,
        isTracked: true,
      },
      {
        id: 2,
        name: 'Express Shipping',
        description: 'Delivers in 1-2 days',
        estimatedDays: 2,
        price: 99.99,
        isTracked: false,
      },
    ]
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: sampleOptions })

    const wrapper = mount(ShippingManagement)
    await flushPromises()

    // Verify that the sample shipping options are rendered properly
    expect(wrapper.text()).toContain('Standard Shipping')
    expect(wrapper.text()).toContain('Delivers in 3-5 days')
    expect(wrapper.text()).toContain('Express Shipping')
    expect(wrapper.text()).toContain('Delivers in 1-2 days')
    expect(wrapper.text()).toContain('49.99 kr')
    expect(wrapper.text()).toContain('99.99 kr')
    // Check tracked status display
    const tags = wrapper.findAll('.tag')
    expect(tags[0].classes('tag-green')).toBe(true)
    expect(tags[0].text()).toBe('Yes')
    expect(tags[1].classes('tag-gray')).toBe(true)
    expect(tags[1].text()).toBe('No')
    // Ensure that the loading indicator is no longer visible
    expect(wrapper.find('.loading-container').exists()).toBe(false)
  })

  it('displays an error message if fetching shipping options fails', async () => {
    ;(ShippingService.getAllShippingOptions as any).mockRejectedValue(new Error('Network error'))

    const wrapper = mount(ShippingManagement)
    await flushPromises()

    expect(wrapper.text()).toContain('Failed to load shipping options')
    // Verify that the retry button is displayed
    expect(wrapper.find('button.btn-secondary').text()).toBe('Retry')
  })

  it('retries fetching shipping options when retry button is clicked', async () => {
    // First fetch fails
    ;(ShippingService.getAllShippingOptions as any).mockRejectedValueOnce(
      new Error('Network error'),
    )

    const wrapper = mount(ShippingManagement)
    await flushPromises()

    expect(wrapper.text()).toContain('Failed to load shipping options')
    expect(ShippingService.getAllShippingOptions).toHaveBeenCalledTimes(1)

    // Setup successful retry
    const sampleOptions = [
      {
        id: 1,
        name: 'Standard Shipping',
        description: 'Test',
        estimatedDays: 3,
        price: 49.99,
        isTracked: false,
      },
    ]
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValueOnce({ data: sampleOptions })

    // Click retry
    await wrapper.find('button.btn-secondary').trigger('click')
    await flushPromises()

    expect(ShippingService.getAllShippingOptions).toHaveBeenCalledTimes(2)
    expect(wrapper.text()).toContain('Standard Shipping')
    expect(wrapper.text()).not.toContain('Failed to load shipping options')
  })

  it('opens the add shipping option form when "Add New Shipping Option" is clicked', async () => {
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: [] })

    const wrapper = mount(ShippingManagement)
    await flushPromises()

    // Initially, the modal should not be visible
    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
    // Click the "Add New Shipping Option" button
    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()

    // Verify the modal appears with the appropriate header text
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)
    expect(wrapper.find('h3').text()).toContain('Add New Shipping Option')

    // Verify form is initialized with default values
    const form = wrapper.find('form')
    expect((form.find('input#name').element as HTMLInputElement).value).toBe('')
    expect((form.find('textarea#description').element as HTMLTextAreaElement).value).toBe('')
    expect((form.find('input#estimatedDays').element as HTMLInputElement).value).toBe('1')
    expect((form.find('input#price').element as HTMLInputElement).value).toBe('0')
    expect((form.find('input[type="checkbox"]').element as HTMLInputElement).checked).toBe(false)
  })

  it('validates form inputs on submit', async () => {
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: [] })
    const wrapper = mount(ShippingManagement)
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()

    // Submit the form without filling in required fields
    await wrapper.find('form').trigger('submit.prevent')
    await nextTick()

    // Expect validation error messages
    expect(wrapper.text()).toContain('Name is required')
    expect(wrapper.text()).toContain('Description is required')

    // Test numeric validation
    const estimatedDaysInput = wrapper.find('input#estimatedDays')
    const priceInput = wrapper.find('input#price')

    await estimatedDaysInput.setValue(0)
    await priceInput.setValue(-1)
    await wrapper.find('form').trigger('submit.prevent')
    await nextTick()

    expect(wrapper.text()).toContain('Estimated days must be at least 1')
    expect(wrapper.text()).toContain('Price cannot be negative')
  })

  it('creates a new shipping option successfully', async () => {
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: [] })
    ;(ShippingService.createShippingOption as any).mockResolvedValue({})

    const wrapper = mount(ShippingManagement)
    await flushPromises()

    // Open form
    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()

    // Fill form
    await wrapper.find('input#name').setValue('New Option')
    await wrapper.find('textarea#description').setValue('New Description')
    await wrapper.find('input#estimatedDays').setValue(2)
    await wrapper.find('input#price').setValue(79.99)
    await wrapper.find('input[type="checkbox"]').setValue(true)

    // Submit form
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(ShippingService.createShippingOption).toHaveBeenCalledWith({
      id: null,
      name: 'New Option',
      description: 'New Description',
      estimatedDays: 2,
      price: 79.99,
      isTracked: true,
    })

    // Modal should close after successful creation
    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
  })

  it('displays error when creating shipping option fails', async () => {
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: [] })
    ;(ShippingService.createShippingOption as any).mockRejectedValue(new Error('Create failed'))

    const wrapper = mount(ShippingManagement)
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()

    await wrapper.find('input#name').setValue('Test Option')
    await wrapper.find('textarea#description').setValue('Test Description')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(wrapper.find('.error-message').exists()).toBe(true)
    expect(wrapper.text()).toContain('Failed to create shipping option')
    // Modal should stay open
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)
  })

  it('updates an existing shipping option when the form is submitted in "edit" mode', async () => {
    const sampleOptions = [
      {
        id: 1,
        name: 'Standard Shipping',
        description: 'Delivers in 3-5 days',
        estimatedDays: 4,
        price: 49.99,
        isTracked: true,
      },
    ]
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: sampleOptions })
    ;(ShippingService.updateShippingOption as any).mockResolvedValue({})

    const wrapper = mount(ShippingManagement)
    await flushPromises()

    // Click the edit button for the first shipping option
    const editButtons = wrapper.findAll('button.btn-icon.edit')
    expect(editButtons.length).toBeGreaterThan(0)
    await editButtons[0].trigger('click')
    await nextTick()

    // The modal header should indicate edit mode
    expect(wrapper.find('h3').text()).toContain('Edit Shipping Option')

    // Verify form is pre-filled with option data
    const form = wrapper.find('form')
    expect((form.find('input#name').element as HTMLInputElement).value).toBe('Standard Shipping')
    expect((form.find('textarea#description').element as HTMLTextAreaElement).value).toBe(
      'Delivers in 3-5 days',
    )
    expect((form.find('input#estimatedDays').element as HTMLInputElement).value).toBe('4')
    expect((form.find('input#price').element as HTMLInputElement).value).toBe('49.99')
    expect((form.find('input[type="checkbox"]').element as HTMLInputElement).checked).toBe(true)

    // Change values
    await form.find('input#name').setValue('Updated Shipping')
    await form.find('input[type="checkbox"]').setValue(false)

    // Submit the form
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(ShippingService.updateShippingOption).toHaveBeenCalledWith(1, {
      id: 1,
      name: 'Updated Shipping',
      description: 'Delivers in 3-5 days',
      estimatedDays: 4,
      price: 49.99,
      isTracked: false,
    })

    // Modal should close after successful update
    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
  })

  it('displays error when updating shipping option fails', async () => {
    const sampleOptions = [
      {
        id: 1,
        name: 'Standard Shipping',
        description: 'Test',
        estimatedDays: 3,
        price: 49.99,
        isTracked: true,
      },
    ]
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: sampleOptions })
    ;(ShippingService.updateShippingOption as any).mockRejectedValue(new Error('Update failed'))

    const wrapper = mount(ShippingManagement)
    await flushPromises()

    await wrapper.find('button.btn-icon.edit').trigger('click')
    await nextTick()

    await wrapper.find('input#name').setValue('Updated Name')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(wrapper.find('.error-message').exists()).toBe(true)
    expect(wrapper.text()).toContain('Failed to update shipping option')
    // Modal should stay open
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)
  })


  it('closes the modal when clicking the backdrop', async () => {
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: [] })
    const wrapper = mount(ShippingManagement)
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)

    await wrapper.find('.modal-backdrop').trigger('click')
    await nextTick()

    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
  })

  it('closes the modal when clicking the close button', async () => {
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: [] })
    const wrapper = mount(ShippingManagement)
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)

    await wrapper.find('.modal-header .btn-close').trigger('click')
    await nextTick()

    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
  })

  it('closes the modal when clicking the cancel button', async () => {
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: [] })
    const wrapper = mount(ShippingManagement)
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)

    await wrapper.find('.form-actions .btn-secondary').trigger('click')
    await nextTick()

    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
  })

  it('displays the empty state when no shipping options exist', async () => {
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: [] })
    const wrapper = mount(ShippingManagement)
    await flushPromises()

    expect(wrapper.find('.empty-state').exists()).toBe(true)
    expect(wrapper.text()).toContain('No shipping options found')
    expect(wrapper.find('.admin-table').exists()).toBe(false)
    expect(wrapper.find('.empty-state .btn-primary').exists()).toBe(true)
    expect(wrapper.find('.empty-state .btn-primary').text()).toBe('Add Your First Shipping Option')
  })

  it('formats price correctly', async () => {
    const sampleOptions = [
      { id: 1, name: 'Test', description: 'Test', estimatedDays: 3, price: 49.9, isTracked: true },
      {
        id: 2,
        name: 'Test 2',
        description: 'Test 2',
        estimatedDays: 2,
        price: 99,
        isTracked: false,
      },
    ]
    ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({ data: sampleOptions })

    const wrapper = mount(ShippingManagement)
    await flushPromises()

    const prices = wrapper.findAll('td').filter((td) => td.text().includes('kr'))
    expect(prices[0].text()).toBe('49.90 kr')
    expect(prices[1].text()).toBe('99.00 kr')
  })
})
