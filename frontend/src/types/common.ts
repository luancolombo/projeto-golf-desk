export type Id = number;

export type LocalDateString = string;
export type LocalTimeString = string;
export type LocalDateTimeString = string;

export type HateoasLink = {
  href: string;
  type?: string;
};

export type HateoasLinks = Record<string, HateoasLink | HateoasLink[]>;

export type HateoasResource = {
  _links?: HateoasLinks;
};
