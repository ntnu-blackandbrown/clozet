// tests/unit/components/admin/categories/CategoryManagement.spec.ts
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import CategoryManagement from '@/views/admin/categories/CategoryManagement.vue'
import { CategoryService } from '@/api/services/CategoryService'
import CreateCategoryModal from '@/components/admin/categories/CreateCategoryModal.vue'

// Define a category type for TypeScript
interface Category {
  id: number;
  name: string;
  description: string;
  parent: { id: number; name: string } | null;
}

// ---------------------------
// Mocks
// ---------------------------
vi.mock('@/api/services/CategoryService', () => ({
  CategoryService: {
    getAllCategories: vi.fn(),
    createCategory: vi.fn(),
    updateCategory: vi.fn(),
    deleteCategory: vi.fn(),
  },
}))

vi.mock('@/components/admin/categories/CreateCategoryModal.vue', () => ({
  default: {
    name: 'CreateCategoryModal',
    template: '<div class="mock-create-modal"></div>',
    props: ['isVisible', 'existingCategories']
  }
}))

// ---------------------------
// Helper to mount the component
// ---------------------------
describe('CategoryManagement.vue', () => {
  let wrapper: VueWrapper<any>

  const createWrapper = async () => {
    wrapper = mount(CategoryManagement)
    // Wait a tick for the onMounted hook to call fetchCategories
    await wrapper.vm.$nextTick()
  }

  beforeEach(() => {
    vi.clearAllMocks()
    vi.spyOn(console, 'error').mockImplementation(() => {})
    vi.spyOn(window, 'confirm').mockImplementation(() => true)
  })

  afterEach(() => {
    wrapper?.unmount()
    vi.restoreAllMocks()
  })

  // ---------------------------
  // Asynchronous States & Data Rendering
  // ---------------------------
  it('renders the loading spinner while categories are being fetched', async () => {
    // Simulate getAllCategories never resolving
    (CategoryService.getAllCategories as any).mockImplementation(() => new Promise(() => {}))
    await createWrapper()

    expect(wrapper.find('[role="status"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('Loading categories...')
  })

  it('renders categories in a table once loaded', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({
      data: [
        { id: 1, name: 'Category A', description: 'Desc A', parent: null },
        { id: 2, name: 'Category B', description: 'Desc B', parent: { id: 1, name: 'Category A' } },
      ],
    })
    await createWrapper()

    const rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(2)
    expect(rows[0].text()).toContain('Category A')
    expect(rows[1].text()).toContain('Category B')
    expect(rows[1].text()).toContain('Category A') // Check parent name
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

    await wrapper.find('button[aria-label="Retry loading categories"]').trigger('click')
    await wrapper.vm.$nextTick()
    expect(wrapper.text()).toContain('No categories found')
  })

  // ---------------------------
  // Modal & Create Category Flow
  // ---------------------------
  it('opens the "Create Category" modal when the Add New Category button is clicked', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()

    const createModal = wrapper.findComponent({ name: 'CreateCategoryModal' })
    expect(createModal.props('isVisible')).toBe(false)

    await wrapper.find('button[aria-label="Add new category"]').trigger('click')
    expect(wrapper.findComponent({ name: 'CreateCategoryModal' }).props('isVisible')).toBe(true)
  })

  it('creates a new category when the create event is emitted', async () => {
    (CategoryService.getAllCategories as any)
      .mockResolvedValueOnce({ data: [] })
      .mockResolvedValueOnce({ data: [] }) // For the second call after creation

    (CategoryService.createCategory as any).mockResolvedValueOnce({})
    await createWrapper()

    const newCategory = { name: 'New Category', description: 'Description', parentId: null }
    await wrapper.findComponent({ name: 'CreateCategoryModal' }).vm.$emit('create', newCategory)

    expect(CategoryService.createCategory).toHaveBeenCalledWith(newCategory)
    expect(CategoryService.getAllCategories).toHaveBeenCalledTimes(2)
  })

  it('handles error when creating a category fails', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    (CategoryService.createCategory as any).mockRejectedValueOnce(new Error('Create error'))
    await createWrapper()

    const newCategory = { name: 'New Category', description: 'Description', parentId: null }
    await wrapper.findComponent({ name: 'CreateCategoryModal' }).vm.$emit('create', newCategory)

    expect(CategoryService.createCategory).toHaveBeenCalledWith(newCategory)
    expect(wrapper.vm.error).toBe('Failed to create category')
  })

  it('cancels category creation when modal is closed', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()

    await wrapper.find('button[aria-label="Add new category"]').trigger('click')
    expect(wrapper.vm.showCreateModal).toBe(true)

    await wrapper.findComponent({ name: 'CreateCategoryModal' }).vm.$emit('close')
    expect(wrapper.vm.showCreateModal).toBe(false)
  })

  // ---------------------------
  // Edit Form Flow & Helper Functions
  // ---------------------------
  it('opens edit form with prefilled data when edit button is clicked', async () => {
    const categories = [
      { id: 1, name: 'Category A', description: 'Desc A', parent: null }
    ] as Category[]
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: categories  })
    await createWrapper()

    await wrapper.find('button.btn-icon.edit').trigger('click')
    expect(wrapper.vm.showEditForm).toBe(true)
    expect(wrapper.vm.editCategoryForm).toEqual({
      id: 1,
      name: 'Category A',
      description: 'Desc A',
      parentId: null
    })
  })

  it('validates the edit form and prevents update when invalid', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    wrapper.vm.showEditForm = true
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: '', // Invalid: missing name
      description: 'A valid description',
      parentId: null,
    }
    await wrapper.vm.$nextTick()

    await wrapper.find('form.category-form').trigger('submit.prevent')
    expect(CategoryService.updateCategory).not.toHaveBeenCalled()
    expect(wrapper.vm.editFormErrors.name).toBe('Category name is required')
  })

  it('validates name length boundaries in edit form', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    wrapper.vm.showEditForm = true

    // Test valid: exactly 3 characters
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'abc',
      description: 'Valid description',
      parentId: null
    }
    expect(wrapper.vm.validateEditForm()).toBe(true)

    // Test invalid: too short (2 characters)
    wrapper.vm.editCategoryForm.name = 'ab'
    expect(wrapper.vm.validateEditForm()).toBe(false)
    expect(wrapper.vm.editFormErrors.name).toBe('Name must be between 3 and 100 characters')

    // Test valid: exactly 100 characters
    wrapper.vm.editCategoryForm.name = 'a'.repeat(100)
    wrapper.vm.editFormErrors = {}  // reset errors
    expect(wrapper.vm.validateEditForm()).toBe(true)
  })

  it('validates description length boundaries in edit form', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    wrapper.vm.showEditForm = true

    // Valid: exactly 255 characters
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'Valid Name',
      description: 'a'.repeat(255),
      parentId: null
    }
    wrapper.vm.editFormErrors = {}
    expect(wrapper.vm.validateEditForm()).toBe(true)

    // Invalid: 256 characters
    wrapper.vm.editCategoryForm.description = 'a'.repeat(256)
    expect(wrapper.vm.validateEditForm()).toBe(false)
    expect(wrapper.vm.editFormErrors.description).toBe('Description cannot exceed 255 characters')
  })

  it('resets the edit form correctly', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'Some Category',
      description: 'Some description',
      parentId: 2
    }
    wrapper.vm.editFormErrors = { name: 'Error' }
    wrapper.vm.resetEditForm()
    expect(wrapper.vm.editCategoryForm).toEqual({
      id: null,
      name: '',
      description: '',
      parentId: null
    })
    expect(wrapper.vm.editFormErrors).toEqual({})
  })

  it('directly calls getParentName and returns correct values', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    expect(wrapper.vm.getParentName({ parent: null })).toBe('-')
    expect(wrapper.vm.getParentName({ parent: { name: 'Parent Category' } })).toBe('Parent Category')
  })

  // ---------------------------
  // Update & Delete Category Flows
  // ---------------------------
  it('successfully updates a category when edit form is submitted with valid data', async () => {
    (CategoryService.getAllCategories as any)
      .mockResolvedValueOnce({ data: [] })
      .mockResolvedValueOnce({ data: [] }) // For the refresh after update

    (CategoryService.updateCategory as any).mockResolvedValueOnce({})
    await createWrapper()

    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'Updated Category',
      description: 'Updated description',
      parentId: null
    }
    wrapper.vm.showEditForm = true
    await wrapper.vm.$nextTick()

    await wrapper.find('form.category-form').trigger('submit.prevent')
    expect(CategoryService.updateCategory).toHaveBeenCalledWith(1, {
      id: 1,
      name: 'Updated Category',
      description: 'Updated description',
      parentId: null
    })
    expect(wrapper.vm.showEditForm).toBe(false)
  })

  it('handles error when updating a category fails', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    (CategoryService.updateCategory as any).mockRejectedValueOnce(new Error('Update error'))
    await createWrapper()

    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'Updated Category',
      description: 'Updated description',
      parentId: null
    }
    wrapper.vm.showEditForm = true
    await wrapper.vm.$nextTick()

    await wrapper.find('form.category-form').trigger('submit.prevent')
    expect(CategoryService.updateCategory).toHaveBeenCalled()
    expect(wrapper.vm.error).toBe('Failed to update category')
  })

  it('prompts for confirmation before deleting a category and aborts if cancelled', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({
      data: [{ id: 1, name: 'Category A', description: 'Desc A', parent: null }],
    })
    await createWrapper()
    vi.spyOn(window, 'confirm').mockImplementationOnce(() => false)
    await wrapper.vm.deleteCategory(1)
    expect(window.confirm).toHaveBeenCalled()
    expect(CategoryService.deleteCategory).not.toHaveBeenCalled()
  })

  it('successfully deletes a category when confirmed', async () => {
    const categories = [
      { id: 1, name: 'Category A', description: 'Desc A', parent: null }
    ] as Category[]
    (CategoryService.getAllCategories as any)
      .mockResolvedValueOnce({ data: categories })
      .mockResolvedValueOnce({ data: [] }) // For the refresh after deletion

    (CategoryService.deleteCategory as any).mockResolvedValueOnce({})
    await createWrapper()

    // Ensure confirm returns true
    vi.spyOn(window, 'confirm').mockReturnValueOnce(true)
    await wrapper.vm.deleteCategory(1)
    expect(window.confirm).toHaveBeenCalled()
    expect(CategoryService.deleteCategory).toHaveBeenCalledWith(1)
    expect(CategoryService.getAllCategories).toHaveBeenCalledTimes(2)
  })

  it('handles error when deleting a category fails', async () => {
    const categories = [
      { id: 1, name: 'Category A', description: 'Desc A', parent: null }
    ] as Category[]
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: categories })
    (CategoryService.deleteCategory as any).mockRejectedValueOnce(new Error('Delete error'))
    await createWrapper()
    vi.spyOn(window, 'confirm').mockReturnValueOnce(true)
    await wrapper.vm.deleteCategory(1)
    expect(CategoryService.deleteCategory).toHaveBeenCalledWith(1)
    expect(wrapper.vm.error).toBe('Failed to delete category')
  })

  it('closes edit form when cancel button is clicked', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()

    wrapper.vm.showEditForm = true
    await wrapper.vm.$nextTick()

    await wrapper.find('button[aria-label="Cancel"]').trigger('click')
    expect(wrapper.vm.showEditForm).toBe(false)
  })

  // ---------------------------
  // Asynchronous Loading States in Create/Update Operations
  // ---------------------------
  it('sets and resets loading state during createCategory', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    let loadingDuringCreate = false;
    (CategoryService.createCategory as any).mockImplementationOnce(async () => {
      loadingDuringCreate = wrapper.vm.isLoading;
      return {}
    })
    await createWrapper()

    await wrapper.vm.createCategory({ name: 'Test', description: 'Test description', parentId: null })
    expect(loadingDuringCreate).toBe(true)
    expect(wrapper.vm.isLoading).toBe(false)
  })

  it('sets and resets loading state during updateCategory', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    let isLoadingDuringUpdate = false;
    (CategoryService.updateCategory as any).mockImplementationOnce(async () => {
      isLoadingDuringUpdate = wrapper.vm.isLoading;
      return {}
    })
    await createWrapper()

    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'Updated Category',
      description: 'Updated description',
      parentId: null
    }
    wrapper.vm.showEditForm = true
    await wrapper.vm.$nextTick()
    await wrapper.find('form.category-form').trigger('submit.prevent')
    expect(isLoadingDuringUpdate).toBe(true)
    expect(wrapper.vm.isLoading).toBe(false)
  })

  // ---------------------------
  // Directly calling handleEditSubmit and resetEditForm
  // ---------------------------
  it('directly calls handleEditSubmit when form is submitted', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    const handleEditSubmitSpy = vi.spyOn(wrapper.vm, 'handleEditSubmit')
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: 'Valid Category',
      description: 'Valid description',
      parentId: null
    }
    wrapper.vm.showEditForm = true
    await wrapper.vm.$nextTick()
    await wrapper.find('form.category-form').trigger('submit.prevent')
    expect(handleEditSubmitSpy).toHaveBeenCalled()
  })
})
