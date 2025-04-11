import * as yup from 'yup'

// User authentication related schemas
export const usernameSchema = yup.string().required('Username is required')

export const emailSchema = yup.string().email('Invalid email').required('Email is required')

export const passwordSchema = yup
  .string()
  .required('Password is required')
  .min(8, 'Password must be at least 8 characters')
  .matches(/[A-Z]/, 'Password must contain at least one uppercase letter')
  .matches(/[a-z]/, 'Password must contain at least one lowercase letter')
  .matches(/[0-9]/, 'Password must contain at least one number')

export const confirmPasswordSchema = (passwordRef: string) =>
  yup
    .string()
    .required('Please confirm your password')
    .oneOf([yup.ref(passwordRef)], 'Passwords must match')

export const nameSchema = yup.string().required('Name is required')

// Phone validation for Norwegian format
export const norwegianPhoneSchema = yup
  .string()
  .matches(/^\+?\d{8,15}$/, 'Please enter a valid phone number')
  .required('Phone number is required')

// Norwegian postal code validation
export const norwegianPostalCodeSchema = yup
  .string()
  .matches(/^\d{4}$/, 'Please enter a valid Norwegian postal code (4 digits)')
  .required('Postal code is required')

// Composite schemas
export const loginSchema = yup.object({
  username: usernameSchema,
  password: passwordSchema,
})

export const registerSchema = yup.object({
  username: usernameSchema,
  firstName: nameSchema,
  lastName: nameSchema,
  email: emailSchema,
  password: passwordSchema,
  confirmPassword: confirmPasswordSchema('password'),
})

export const changePasswordSchema = yup.object({
  currentPassword: yup.string().required('Current password is required'),
  newPassword: passwordSchema,
  confirmPassword: confirmPasswordSchema('newPassword'),
})

export const shippingDetailsSchema = yup.object({
  firstName: nameSchema,
  lastName: nameSchema,
  streetAddress: yup.string().required('Street address is required'),
  postalCode: norwegianPostalCodeSchema,
  city: yup.string().required('City is required'),
  country: yup.string().required('Country is required'),
  phone: norwegianPhoneSchema,
})

export const vippsPaymentSchema = yup.object({
  phoneNumber: yup
    .string()
    .matches(
      /^\+47[0-9]{8}$/,
      'Please enter a valid Norwegian phone number (+47 followed by 8 digits)',
    )
    .required('Phone number is required'),
  pincode: yup
    .string()
    .matches(/^\d{4}$/, 'PIN must be 4 digits')
    .required('PIN is required'),
})

// Product validation schema
export const productSchema = yup.object({
  title: yup.string().required('Title is required'),
  shortDescription: yup.string().required('Short description is required'),
  longDescription: yup.string().required('Long description is required'),
  price: yup
    .number()
    .typeError('Price must be a number') // Added typeError for better feedback
    .required('Price is required')
    .positive('Price must be positive')
    .min(0, 'Price must be at least 0'),
  categoryId: yup.string().required('Category is required'), // Keep as string if value is ID
  locationId: yup.string().required('Location is required'), // Keep as string if value is ID
  shippingOptionId: yup.string().required('Shipping option is required'), // Keep as string if value is ID
  condition: yup.string().required('Condition is required'),
  size: yup.string().required('Size is required'),
  brand: yup.string().required('Brand is required'),
  color: yup.string().required('Color is required'),
  isVippsPaymentEnabled: yup.boolean(),
  // Assuming images are handled separately and not part of this schema validation directly
})
