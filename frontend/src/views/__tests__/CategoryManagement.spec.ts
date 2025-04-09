import { mount, flushPromises } from '@vue/test-utils'
import { nextTick } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import CategoryManagement from '@/views/admin/categories/CategoryManagement.vue'
import { CategoryService } from '@/api/services/CategoryService'

// Mock the CategoryService methods
vi.mock('@/api/services/CategoryService', () => ({
  CategoryService: {
    getAllCategories: vi.fn(),
    createCategory: vi.fn(),
    updateCategory: vi.fn(),
    deleteCategory: vi.fn(),
  },
}))

describe('CategoryManagement', () => {
  beforeEach(() => {
    // Clear all mocks before each test to avoid carry-over state
    vi.clearAllMocks()
    // Restore mocks that might have been changed in specific tests
    vi.spyOn(window, 'confirm').mockRestore()
  })

  it('fetches categories on mount and displays them', async () => {
    const sampleCategories = [
      { id: 1, name: 'Cat1', description: 'Desc1', parent: null },
      { id: 2, name: 'Cat2', description: 'Desc2', parent: { id: 1, name: 'Cat1' } },
    ]
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: sampleCategories })

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    // Check that sample category data appears in the rendered table
    expect(wrapper.text()).toContain('Cat1')
    expect(wrapper.text()).toContain('Desc1')
    expect(wrapper.text()).toContain('Cat2')
    expect(wrapper.text()).toContain('Desc2')
    // Confirm the loading indicator is no longer visible
    expect(wrapper.find('.loading-container').exists()).toBe(false)
  })

  it('displays an error message if fetching categories fails', async () => {
    ;(CategoryService.getAllCategories as any).mockRejectedValue(new Error('Network error'))

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    expect(wrapper.text()).toContain('Failed to load categories')
    // Verify the retry button is rendered
    expect(wrapper.find('button.btn-secondary').text()).toContain('Retry')
  })

  it('retries fetching categories when the retry button is clicked', async () => {
    // First fetch fails
    ;(CategoryService.getAllCategories as any).mockRejectedValueOnce(new Error('Network error'))

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    expect(wrapper.text()).toContain('Failed to load categories')
    expect(CategoryService.getAllCategories).toHaveBeenCalledTimes(1)

    // Mock successful fetch for the retry
    const sampleCategories = [{ id: 1, name: 'Cat1', description: 'Desc1', parent: null }]
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: sampleCategories })

    // Click the retry button
    await wrapper.find('button.btn-secondary').trigger('click')
    await flushPromises()

    // Check that fetch was called again and data is displayed
    expect(CategoryService.getAllCategories).toHaveBeenCalledTimes(2)
    expect(wrapper.text()).not.toContain('Failed to load categories')
    expect(wrapper.text()).toContain('Cat1')
  })

  it('opens the add category form when "Add New Category" is clicked', async () => {
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: [] })

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    // Initially, the modal should not be visible
    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
    // Click the "Add New Category" button
    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()

    // Verify the modal appears with the correct header
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)
    expect(wrapper.find('h3').text()).toContain('Add New Category')
  })

  it('validates form inputs on submit', async () => {
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: [] })
    const wrapper = mount(CategoryManagement)
    await flushPromises()

    // Open the add form
    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()

    // Submit the form without filling required fields (name and description)
    await wrapper.find('form').trigger('submit.prevent')
    await nextTick()

    // Expect to see validation error messages
    expect(wrapper.text()).toContain('Category name is required')
    expect(wrapper.text()).toContain('Description is required')
  })

  it('creates a new category when the form is submitted in "add" mode', async () => {
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: [] })
    ;(CategoryService.createCategory as any).mockResolvedValue({})

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    // Open the add form
    await wrapper.find('.btn-primary').trigger('click')
    await nextTick()

    // Fill in the form inputs
    const nameInput = wrapper.find('input#name')
    const descInput = wrapper.find('textarea#description')
    const parentSelect = wrapper.find('select#parentId')

    await nameInput.setValue('New Category')
    await descInput.setValue('New Description')
    // Leave parentId at its default (null)

    // Submit the form; handleSubmit should trigger createCategory
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(CategoryService.createCategory).toHaveBeenCalledWith({
      id: null,
      name: 'New Category',
      description: 'New Description',
      parentId: null,
    })
    // Modal should close after successful creation
    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
  })

  it('displays an error message if creating a category fails', async () => {
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: [] })
    ;(CategoryService.createCategory as any).mockRejectedValue(new Error('Create failed'))

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click') // Open add form
    await nextTick()

    await wrapper.find('input#name').setValue('Fail Cat')
    await wrapper.find('textarea#description').setValue('Fail Desc')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    // Check that the error message is displayed (usually near the top or form)
    // Adjust selector based on where the error message actually appears
    expect(wrapper.find('.error-message').exists()).toBe(true)
    expect(wrapper.text()).toContain('Failed to create category')
    // Modal should remain open on failure
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)
  })

  it('updates an existing category when the form is submitted in "edit" mode', async () => {
    const sampleCategories = [{ id: 1, name: 'Cat1', description: 'Desc1', parent: null }]
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: sampleCategories })
    ;(CategoryService.updateCategory as any).mockResolvedValue({})

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    // Click the edit button for the first category
    const editButtons = wrapper.findAll('button.btn-icon.edit')
    expect(editButtons.length).toBeGreaterThan(0)
    await editButtons[0].trigger('click')
    await nextTick()

    // The modal should now be in edit mode with prefilled data
    expect(wrapper.find('h3').text()).toContain('Edit Category')

    const nameInput = wrapper.find<HTMLInputElement>('input#name')
    expect(nameInput.element.value).toBe('Cat1')
    // Change the category name
    await nameInput.setValue('Updated Cat1')

    // Submit the form; handleSubmit should trigger updateCategory
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(CategoryService.updateCategory).toHaveBeenCalledWith(1, {
      id: 1,
      name: 'Updated Cat1',
      description: 'Desc1',
      parentId: null,
    })
    // Modal should close after successful update
    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
  })

  it('displays an error message if updating a category fails', async () => {
    const sampleCategories = [{ id: 1, name: 'Cat1', description: 'Desc1', parent: null }]
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: sampleCategories })
    ;(CategoryService.updateCategory as any).mockRejectedValue(new Error('Update failed'))

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    // Open edit form for the first category
    await wrapper.findAll('button.btn-icon.edit')[0].trigger('click')
    await nextTick()

    await wrapper.find('input#name').setValue('Update Fail Cat')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(wrapper.find('.error-message').exists()).toBe(true)
    expect(wrapper.text()).toContain('Failed to update category')
    // Modal should remain open
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)
  })

  it('deletes a category after confirmation', async () => {
    const sampleCategories = [{ id: 1, name: 'Cat1', description: 'Desc1', parent: null }]
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: sampleCategories })
    ;(CategoryService.deleteCategory as any).mockResolvedValue({})

    // Mock the confirm dialog to always return true
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true)

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    // Trigger deletion by clicking the delete button
    const deleteButton = wrapper.find('button.btn-icon.delete')
    await deleteButton.trigger('click')
    await flushPromises()

    expect(confirmSpy).toHaveBeenCalled()
    expect(CategoryService.deleteCategory).toHaveBeenCalledWith(1)

    // Restore the original confirm function
    confirmSpy.mockRestore() // Use mockRestore instead of restore
  })

  it('does not delete a category if confirmation is cancelled', async () => {
    const sampleCategories = [{ id: 1, name: 'Cat1', description: 'Desc1', parent: null }]
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: sampleCategories })

    // Mock confirm to return false (user cancels)
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(false)

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    await wrapper.find('button.btn-icon.delete').trigger('click')
    await flushPromises()

    expect(confirmSpy).toHaveBeenCalled()
    // deleteCategory should NOT have been called
    expect(CategoryService.deleteCategory).not.toHaveBeenCalled()

    confirmSpy.mockRestore()
  })

  it('displays an error message if deleting a category fails', async () => {
    const sampleCategories = [{ id: 1, name: 'Cat1', description: 'Desc1', parent: null }]
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: sampleCategories })
    ;(CategoryService.deleteCategory as any).mockRejectedValue(new Error('Delete failed'))

    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true)

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    await wrapper.find('button.btn-icon.delete').trigger('click')
    await flushPromises()

    expect(confirmSpy).toHaveBeenCalled()
    expect(CategoryService.deleteCategory).toHaveBeenCalledWith(1)
    // Check for the error message
    expect(wrapper.find('.error-message').exists()).toBe(true)
    expect(wrapper.text()).toContain('Failed to delete category')

    confirmSpy.mockRestore()
  })

  it('closes the modal when clicking the backdrop', async () => {
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: [] })
    const wrapper = mount(CategoryManagement)
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click') // Open add form
    await nextTick()
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)

    // Simulate click on the backdrop itself
    await wrapper.find('.modal-backdrop').trigger('click')
    await nextTick()

    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
  })

  it('closes the modal when clicking the close button', async () => {
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: [] })
    const wrapper = mount(CategoryManagement)
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click') // Open add form
    await nextTick()
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)

    // Click the '×' button in the modal header
    await wrapper.find('.modal-header .btn-close').trigger('click')
    await nextTick()

    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
  })

  it('closes the modal when clicking the cancel button', async () => {
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: [] })
    const wrapper = mount(CategoryManagement)
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click') // Open add form
    await nextTick()
    expect(wrapper.find('.modal-backdrop').exists()).toBe(true)

    // Click the 'Cancel' button in the form actions
    await wrapper.find('.form-actions .btn-secondary').trigger('click')
    await nextTick()

    expect(wrapper.find('.modal-backdrop').exists()).toBe(false)
  })

  it('displays the empty state when no categories are fetched', async () => {
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: [] })
    const wrapper = mount(CategoryManagement)
    await flushPromises()

    expect(wrapper.find('.empty-state').exists()).toBe(true)
    expect(wrapper.text()).toContain('No categories found')
    expect(wrapper.find('.admin-table').exists()).toBe(false)
    expect(wrapper.find('.loading-container').exists()).toBe(false)
    // Check if the "Add Your First Category" button exists in the empty state
    expect(wrapper.find('.empty-state .btn-primary').exists()).toBe(true)
  })

  it('allows selecting a parent category when adding/editing', async () => {
    const sampleCategories = [
      { id: 1, name: 'Parent Cat', description: 'Parent Desc', parent: null },
      {
        id: 2,
        name: 'Child Cat',
        description: 'Child Desc',
        parent: { id: 1, name: 'Parent Cat' },
      },
    ]
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: sampleCategories })
    ;(CategoryService.createCategory as any).mockResolvedValue({})
    ;(CategoryService.updateCategory as any).mockResolvedValue({})

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    // Test Adding with Parent
    await wrapper.find('.btn-primary').trigger('click') // Open add form
    await nextTick()

    await wrapper.find('input#name').setValue('New Child')
    await wrapper.find('textarea#description').setValue('New Desc')
    const parentSelectAdd = wrapper.find<HTMLSelectElement>('select#parentId')
    await parentSelectAdd.setValue(1) // Select 'Parent Cat' (ID 1)

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(CategoryService.createCategory).toHaveBeenCalledWith(
      expect.objectContaining({
        name: 'New Child',
        parentId: 1, // Expect number instead of string
      }),
    )

    // Test Editing to add Parent
    await wrapper.findAll('button.btn-icon.edit')[0].trigger('click') // Edit 'Parent Cat' (ID 1) - assume it was re-fetched
    await nextTick()
    // Can't set parent to itself - let's edit the second one instead
    await wrapper.find('.modal-backdrop').trigger('click') // Close modal first
    await nextTick()

    await wrapper.findAll('button.btn-icon.edit')[1].trigger('click') // Edit 'Child Cat' (ID 2)
    await nextTick()

    const parentSelectEdit = wrapper.find<HTMLSelectElement>('select#parentId')
    // It should already have parent 1 selected because of the initial data
    expect(parentSelectEdit.element.value).toBe('1')
  })

  it('disables selecting the category itself as a parent', async () => {
    const sampleCategories = [
      { id: 1, name: 'Cat1', description: 'Desc1', parent: null },
      { id: 2, name: 'Cat2', description: 'Desc2', parent: null },
    ]
    ;(CategoryService.getAllCategories as any).mockResolvedValue({ data: sampleCategories })

    const wrapper = mount(CategoryManagement)
    await flushPromises()

    // Open edit form for 'Cat1'
    await wrapper.findAll('button.btn-icon.edit')[0].trigger('click')
    await nextTick()

    const parentSelect = wrapper.find('select#parentId')
    const cat1Option = parentSelect.find('option[value="1"]')

    expect(cat1Option.exists()).toBe(true)
    expect((cat1Option.element as HTMLOptionElement).disabled).toBe(true)

    // Ensure other options are not disabled
    const cat2Option = parentSelect.find('option[value="2"]')
    expect(cat2Option.exists()).toBe(true)
    expect((cat2Option.element as HTMLOptionElement).disabled).toBe(false)

    // Find the "None" option by text content
    const noneOption = wrapper.findAll('option').find((o) => o.text().includes('None'))
    expect(noneOption).toBeTruthy()
    expect((noneOption?.element as HTMLOptionElement).disabled).toBe(false)
  })
})
