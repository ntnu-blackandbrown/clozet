// tests/unit/components/admin/categories/CategoryManagement.spec.ts
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import CategoryManagement from '@/views/admin/categories/CategoryManagement.vue'
import { CategoryService } from '@/api/services/CategoryService'
import CreateCategoryModal from '@/components/admin/categories/CreateCategoryModal.vue'

// Define a category type to help with TypeScript errors
interface Category {
  id: number;
  name: string;
  description: string;
  parent: { id: number; name: string } | null;
}

// Mock the CategoryService methods
vi.mock('@/api/services/CategoryService', () => ({
  CategoryService: {
    getAllCategories: vi.fn(),
    createCategory: vi.fn(),
    updateCategory: vi.fn(),
    deleteCategory: vi.fn(),
  },
}))

// Mock the CreateCategoryModal component
vi.mock('@/components/admin/categories/CreateCategoryModal.vue', () => ({
  default: {
    name: 'CreateCategoryModal',
    template: '<div class="mock-create-modal"></div>',
    props: ['isVisible', 'existingCategories']
  }
}))

describe('CategoryManagement.vue', () => {
  let wrapper: VueWrapper<any>

  // Helper to mount the component
  const createWrapper = async () => {
    wrapper = mount(CategoryManagement)
    // Wait a tick for the onMounted hook (which calls fetchCategories)
    await wrapper.vm.$nextTick()
  }

  beforeEach(() => {
    vi.clearAllMocks()
    // Mock console.error to avoid cluttering test output
    vi.spyOn(console, 'error').mockImplementation(() => {})
  })

  afterEach(() => {
    wrapper?.unmount()
    vi.restoreAllMocks()
  })

  it('renders the loading spinner while categories are being fetched', async () => {
    // Simulate getAllCategories never resolving (loading state)
    (CategoryService.getAllCategories as any).mockImplementation(() => new Promise(() => {}))
    await createWrapper()

    expect(wrapper.find('[role="status"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('Loading categories...')
  })

  it('renders categories in a table once loaded', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({
      data: [
        { id: 1, name: 'Category A', description: 'Desc A', parent: null },
        {
          id: 2,
          name: 'Category B',
          description: 'Desc B',
          parent: { id: 1, name: 'Category A' },
        },
      ],
    })

    await createWrapper()

    const rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(2)
    expect(rows[0].text()).toContain('Category A')
    expect(rows[1].text()).toContain('Category B')
    expect(rows[1].text()).toContain('Category A') // Parent name
  })

  it('renders an empty state when no categories exist', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    expect(wrapper.text()).toContain('No categories found')
  })

  it('shows an error message if fetching categories fails', async () => {
    (CategoryService.getAllCategories as any).mockRejectedValueOnce(new Error('API error'))
    await createWrapper()
    expect(wrapper.text()).toContain('Failed to load categories')
  })

  it('allows retrying after load failure', async () => {
    (CategoryService.getAllCategories as any)
      .mockRejectedValueOnce(new Error('API error'))
      .mockResolvedValueOnce({ data: [] })

    await createWrapper()
    expect(wrapper.text()).toContain('Failed to load categories')

    // Click the "Retry" button
    await wrapper.find('button[aria-label="Retry loading categories"]').trigger('click')
    // Wait for the re-fetch and update
    await wrapper.vm.$nextTick()
    expect(wrapper.text()).toContain('No categories found')
  })

  it('opens the "Create Category" modal when the Add New Category button is clicked', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    const createModal = wrapper.findComponent({ name: 'CreateCategoryModal' })
    // Modal is initially hidden (is-visible prop should be false)
    expect(createModal.props('isVisible')).toBe(false)

    // Click "Add New Category" button
    await wrapper.find('button[aria-label="Add new category"]').trigger('click')
    expect(wrapper.findComponent({ name: 'CreateCategoryModal' }).props('isVisible')).toBe(true)
  })

  it('validates the edit form and prevents update when invalid', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    wrapper.vm.showEditForm = true
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: '', // Invalid: name is required
      description: 'A valid description',
      parentId: null,
    }
    await wrapper.vm.$nextTick()

    await wrapper.find('form.category-form').trigger('submit.prevent')

    expect(CategoryService.updateCategory).not.toHaveBeenCalled()
    expect(wrapper.vm.editFormErrors.name).toBe('Category name is required')
  })

  it('prompts for confirmation before deleting a category and aborts if cancelled', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({
      data: [{ id: 1, name: 'Category A', description: 'Desc A', parent: null }],
    })
    await createWrapper()

    global.confirm = vi.fn().mockReturnValueOnce(false)
    await wrapper.vm.deleteCategory(1)
    expect(global.confirm).toHaveBeenCalled()
    expect(CategoryService.deleteCategory).not.toHaveBeenCalled()
  })

  it('returns "-" if a category has no parent', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({
      data: [{ id: 1, name: 'Category A', description: 'Desc', parent: null }],
    })
    await createWrapper()
    const rowText = wrapper.find('tbody tr').text()
    expect(rowText).toContain('-')
  })

  // New tests to improve coverage

  it('creates a new category when form is submitted', async () => {
    // Setup mocks
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    ;(CategoryService.createCategory as any).mockResolvedValueOnce({})

    await createWrapper()

    // Simulate the create event from the modal
    const newCategory = { name: 'New Category', description: 'Description', parentId: null }
    await wrapper.findComponent({ name: 'CreateCategoryModal' }).vm.$emit('create', newCategory)

    expect(CategoryService.createCategory).toHaveBeenCalledWith(newCategory)
    expect(CategoryService.getAllCategories).toHaveBeenCalledTimes(2) // Initial + after create
  })

  it('handles error when creating a category fails', async () => {
    // Setup mocks
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    ;(CategoryService.createCategory as any).mockRejectedValueOnce(new Error('Create error'))

    await createWrapper()

    // Simulate the create event from the modal
    const newCategory = { name: 'New Category', description: 'Description', parentId: null }
    await wrapper.findComponent({ name: 'CreateCategoryModal' }).vm.$emit('create', newCategory)

    expect(CategoryService.createCategory).toHaveBeenCalledWith(newCategory)
    expect(wrapper.vm.error).toBe('Failed to create category')
  })

  it('cancels category creation when modal is closed', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()

    // Open the modal first
    await wrapper.find('button[aria-label="Add new category"]').trigger('click')
    expect(wrapper.vm.showCreateModal).toBe(true)

    // Close the modal
    await wrapper.findComponent({ name: 'CreateCategoryModal' }).vm.$emit('close')
    expect(wrapper.vm.showCreateModal).toBe(false)
  })

  it('opens edit form with prefilled data when edit button is clicked', async () => {
    const categories: Category[] = [
      { id: 1, name: 'Category A', description: 'Desc A', parent: null }
    ];
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: categories })

    await createWrapper()

    // Click edit button
    await wrapper.find('button.btn-icon.edit').trigger('click')

    // Check if edit form is shown with correct data
    expect(wrapper.vm.showEditForm).toBe(true)
    expect(wrapper.vm.editCategoryForm).toEqual({
      id: 1,
      name: 'Category A',
      description: 'Desc A',
      parentId: null
    })
  })

  it('handles error when updating a category fails', async () => {
    // Setup mocks
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    ;(CategoryService.updateCategory as any).mockRejectedValueOnce(new Error('Update error'))

    await createWrapper()

    // Setup edit form with valid data
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'Updated Category',
      description: 'Updated description',
      parentId: null
    }
    wrapper.vm.showEditForm = true
    await wrapper.vm.$nextTick()

    // Submit the form
    await wrapper.find('form.category-form').trigger('submit.prevent')

    expect(CategoryService.updateCategory).toHaveBeenCalled()
    expect(wrapper.vm.error).toBe('Failed to update category')
  })

  it('validates that description is required in edit form', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()

    wrapper.vm.showEditForm = true
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'Valid Name',
      description: '', // Invalid: description is required
      parentId: null
    }
    await wrapper.vm.$nextTick()

    await wrapper.find('form.category-form').trigger('submit.prevent')

    expect(CategoryService.updateCategory).not.toHaveBeenCalled()
    expect(wrapper.vm.editFormErrors.description).toBe('Description is required')
  })

  it('validates name length in edit form', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()

    wrapper.vm.showEditForm = true
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'ab', // Invalid: too short
      description: 'Valid description',
      parentId: null
    }
    await wrapper.vm.$nextTick()

    await wrapper.find('form.category-form').trigger('submit.prevent')

    expect(CategoryService.updateCategory).not.toHaveBeenCalled()
    expect(wrapper.vm.editFormErrors.name).toBe('Name must be between 3 and 100 characters')
  })

  it('validates description length in edit form', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()

    wrapper.vm.showEditForm = true
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'Valid Name',
      description: 'a'.repeat(256), // Invalid: too long
      parentId: null
    }
    await wrapper.vm.$nextTick()

    await wrapper.find('form.category-form').trigger('submit.prevent')

    expect(CategoryService.updateCategory).not.toHaveBeenCalled()
    expect(wrapper.vm.editFormErrors.description).toBe('Description cannot exceed 255 characters')
  })

  it('successfully deletes a category when confirmed', async () => {
    // Setup mocks
    const categories: Category[] = [
      { id: 1, name: 'Category A', description: 'Desc A', parent: null }
    ];
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: categories })
    ;(CategoryService.deleteCategory as any).mockResolvedValueOnce({})

    await createWrapper()

    // Mock confirm to return true
    global.confirm = vi.fn().mockReturnValueOnce(true)

    await wrapper.vm.deleteCategory(1)

    expect(global.confirm).toHaveBeenCalled()
    expect(CategoryService.deleteCategory).toHaveBeenCalledWith(1)
    expect(CategoryService.getAllCategories).toHaveBeenCalledTimes(2) // Initial + after delete
  })

  it('handles error when deleting a category fails', async () => {
    // Setup mocks
    const categories: Category[] = [
      { id: 1, name: 'Category A', description: 'Desc A', parent: null }
    ];
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: categories })
    ;(CategoryService.deleteCategory as any).mockRejectedValueOnce(new Error('Delete error'))

    await createWrapper()

    // Mock confirm to return true
    global.confirm = vi.fn().mockReturnValueOnce(true)

    await wrapper.vm.deleteCategory(1)

    expect(CategoryService.deleteCategory).toHaveBeenCalledWith(1)
    expect(wrapper.vm.error).toBe('Failed to delete category')
  })

  it('closes edit form when cancel button is clicked', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()

    // Open edit form
    wrapper.vm.showEditForm = true
    await wrapper.vm.$nextTick()

    // Click cancel button
    await wrapper.find('button[aria-label="Cancel"]').trigger('click')

    expect(wrapper.vm.showEditForm).toBe(false)
  })

  it('resets edit form when opening it for a new edit', async () => {
    const categories: Category[] = [
      { id: 1, name: 'Category A', description: 'Desc A', parent: null },
      { id: 2, name: 'Category B', description: 'Desc B', parent: null }
    ];
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: categories })

    await createWrapper()

    // First edit Category A
    wrapper.vm.editCategory(categories[0])
    expect(wrapper.vm.editCategoryForm.id).toBe(1)

    // Then edit Category B
    wrapper.vm.editCategory(categories[1])

    // Check if form was reset before being populated with new data
    expect(wrapper.vm.editCategoryForm.id).toBe(2)
    expect(wrapper.vm.editFormErrors).toEqual({})
  })

  it('sets loading state when appropriate', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })

    await createWrapper()

    // Initially loading should be false after mounting (since getAllCategories resolves)
    expect(wrapper.vm.isLoading).toBe(false)

    // Simplified approach to test loading state
    let isLoadingDuringOperation = false;

    (CategoryService.createCategory as any).mockImplementationOnce(async () => {
      // Capture loading state during the operation
      isLoadingDuringOperation = wrapper.vm.isLoading;
      return {};
    });

    const newCategory = { name: 'New Category', description: 'Description', parentId: null }
    await wrapper.vm.createCategory(newCategory)

    // Verify loading was true during the operation
    expect(isLoadingDuringOperation).toBe(true)
    // Loading should be false after operation completes
    expect(wrapper.vm.isLoading).toBe(false)
  })
})
