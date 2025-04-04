export const badgeColors = {
  price: {
    color: '#F1E7CA',
    textColor: '#3A4951',
    borderColor: '#3A4951',
    hoverColor: '#F1E7CA',
    hoverTextColor: '#3A4951',
    hoverBorderColor: '#3A4951',
    activeColor: '#F1E7CA',
    activeTextColor: '#3A4951',
    activeBorderColor: '#3A4951'
  },
  category: {
    color: '#C3D7CC',
    textColor: '#3A4951',
    borderColor: '#3A4951',
    hoverColor: '#B3C7BC',
    hoverTextColor: '#3A4951',
    hoverBorderColor: '#3A4951',
    activeColor: '#A3B7AC',
    activeTextColor: '#3A4951',
    activeBorderColor: '#3A4951'
  },
  location: {
    color: '#E4EAE7',
    textColor: '#2D353F',
    borderColor: '#2D353F',
    hoverColor: '#D4DAE7',
    hoverTextColor: '#2D353F',
    hoverBorderColor: '#2D353F',
    activeColor: '#C4CAE7',
    activeTextColor: '#2D353F',
    activeBorderColor: '#2D353F'
  },
  seller: {
    color: '#F1E7CA',
    textColor: '#3A4951',
    borderColor: '#3A4951',
    hoverColor: '#E5D9B8',
    hoverTextColor: '#3A4951',
    hoverBorderColor: '#3A4951',
    activeColor: '#D9CAA6',
    activeTextColor: '#3A4951',
    activeBorderColor: '#3A4951'
  },
  shipping: {
    color: '#C3D7CC',
    textColor: '#3A4951',
    borderColor: '#3A4951',
    hoverColor: '#B3C7BC',
    hoverTextColor: '#3A4951',
    hoverBorderColor: '#3A4951',
    activeColor: '#A3B7AC',
    activeTextColor: '#3A4951',
    activeBorderColor: '#3A4951'
  },
  availability: {
    color: '#E4EAE7',
    textColor: '#2D353F',
    borderColor: '#2D353F',
    hoverColor: '#D4DAE7',
    hoverTextColor: '#2D353F',
    hoverBorderColor: '#2D353F',
    activeColor: '#C4CAE7',
    activeTextColor: '#2D353F',
    activeBorderColor: '#2D353F'
  }
} as const

export type BadgeType = keyof typeof badgeColors
