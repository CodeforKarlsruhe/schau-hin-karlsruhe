import { SchauHinFrontendPage } from './app.po';

describe('schau-hin-frontend App', function() {
  let page: SchauHinFrontendPage;

  beforeEach(() => {
    page = new SchauHinFrontendPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
