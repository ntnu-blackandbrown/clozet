import { mount, flushPromises } from '@vue/test-utils'
import { nextTick } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import LocationManagement from '@/views/admin/locations/LocationManagement.vue'
import { LocationService } from '@/api/services/LocationService'

// Mock the LocationService methods
vi.mock('@/api/services/LocationService', () => ({
  LocationService: {
    getAllLocations: vi.fn(),
    createLocation: vi.fn(),
    updateLocation: vi.fn(),
    deleteLocation: vi.fn(),
  }
}))

describe('LocationManagement', () => {
  beforeEach(() => {
    // Clear all previous mocks before each test
    vi.clearAllMocks()
  })

  it('fetches locations on mount and displays them', async () => {
    const sampleLocations = [
      { id: 1, city: 'City1', region: 'Region1', latitude: 45.0, longitude: 90.0 },
      { id: 2, city: 'City2', region: 'Region2', latitude: 30.0, longitude: 70.0 },
    ]
    ;(LocationService.getAllLocations as any).mockResolvedValue({ data: sampleLocations })

    const wrapper = mount(LocationManagement)
    await flushPromises()

    // Check that sample location data appears in the rendered table
    expect(wrapper.text()).toContain('City1')
    expect(wrapper.text()).toContain('Region1')
    expect(wrapper.text()).toContain('City2')
    expect(wrapper.text()).toContain('Region2')
    // Ensure the loading indicator is gone
    expect(wrapper.find('.loading-container').exists()).toBe(false)
  })

  it('displays an error message if fetching locations fails', async () => {
    ;(LocationService.getAllLocations as any).mockRejectedValue(new Error('Network error'))

    const wrapper = mount(LocationManagement)
    await flushPromises()

    expect(wrapper.text()).toContain('Failed to load locations')
    // Check that there is a retry button in the error message
    const retryButton = wrapper.find('.btn-secondary')
    expect(retryButton.exists()).toBe(true)
  })

  it('opens the add location form when "Add New Location" is clicked', async () => {
    ;(LocationService.getAllLocations as any).mockResolvedValue({ data: [] })

    const wrapper = mount(LocationManagement)
    await flushPromises()

    // Initially, the modal should not be visible
    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
    // Click the "Add New Location" button
    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()

    // Verify the modal appears with the appropriate header
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)
    expect(wrapper.find('h3').text()).toContain('Add New Location')
  })

  it('validates form inputs on submit', async () => {
    ;(LocationService.getAllLocations as any).mockResolvedValue({ data: [] })
    const wrapper = mount(LocationManagement)
    await flushPromises()

    // Open form by clicking the "Add New Location" button
    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()

    // Submit the form without filling in required fields
    await wrapper.find('form').trigger('submit.prevent')
    await nextTick()

    // Expect error messages from form validation to be visible
    expect(wrapper.text()).toContain('City name is required')
    expect(wrapper.text()).toContain('Region is required')
  })

  it('creates a new location when the form is submitted in "add" mode', async () => {
    ;(LocationService.getAllLocations as any).mockResolvedValue({ data: [] })
    ;(LocationService.createLocation as any).mockResolvedValue({})

    const wrapper = mount(LocationManagement)
    await flushPromises()

    // Open the add form
    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()

    // Fill in form fields
    const cityInput = wrapper.find('input#city')
    const regionInput = wrapper.find('input#region')
    const latitudeInput = wrapper.find('input#latitude')
    const longitudeInput = wrapper.find('input#longitude')

    await cityInput.setValue('New City')
    await regionInput.setValue('New Region')
    await latitudeInput.setValue(50.0)
    await longitudeInput.setValue(100.0)

    // Submit the form; handleSubmit should call createLocation
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(LocationService.createLocation).toHaveBeenCalledWith({
      id: null,
      city: 'New City',
      region: 'New Region',
      latitude: 50.0,
      longitude: 100.0,
    })
  })

  it('updates an existing location when the form is submitted in "edit" mode', async () => {
    const sampleLocations = [
      { id: 1, city: 'City1', region: 'Region1', latitude: 45.0, longitude: 90.0 },
    ]
    ;(LocationService.getAllLocations as any).mockResolvedValue({ data: sampleLocations })
    ;(LocationService.updateLocation as any).mockResolvedValue({})

    const wrapper = mount(LocationManagement)
    await flushPromises()

    // Click the edit button for the first location
    const editButtons = wrapper.findAll('button.btn-icon.edit')
    expect(editButtons.length).toBeGreaterThan(0)
    await editButtons[0].trigger('click')
    await nextTick()

    // The modal should now be in edit mode with pre-filled data
    expect(wrapper.find('h3').text()).toContain('Edit Location')
    const cityInput = wrapper.find<HTMLInputElement>('input#city')
    expect(cityInput.element.value).toBe('City1')
    // Change the city name
    await cityInput.setValue('Updated City')

    // Submit the form; handleSubmit should trigger updateLocation
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(LocationService.updateLocation).toHaveBeenCalledWith(1, {
      id: 1,
      city: 'Updated City',
      region: 'Region1',
      latitude: 45.0,
      longitude: 90.0,
    })
  })

  it('deletes a location after confirmation', async () => {
    const sampleLocations = [
      { id: 1, city: 'City1', region: 'Region1', latitude: 45.0, longitude: 90.0 },
    ]
    ;(LocationService.getAllLocations as any).mockResolvedValue({ data: sampleLocations })
    ;(LocationService.deleteLocation as any).mockResolvedValue({})

    // Mock the confirm dialog to return true
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true)

    const wrapper = mount(LocationManagement)
    await flushPromises()

    // Trigger deletion by clicking the delete button
    const deleteButton = wrapper.find('button.btn-icon.delete')
    await deleteButton.trigger('click')
    await flushPromises()

    expect(confirmSpy).toHaveBeenCalled()
    expect(LocationService.deleteLocation).toHaveBeenCalledWith(1)

    // Restore the original confirm function
    confirmSpy.mockRestore()
  })
})
