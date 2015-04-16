package parser;

public class WikiParamSet
{
    private String pageId = "";
    private String keyword1 = "";
    private String keyword2 = "";

    public WikiParamSet()
    {}
    public WikiParamSet(String pageId, String keyword1, String keyword2)
    {
        this.pageId = pageId;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
    }

    public String getPageId()
    {

        return pageId;
    }

    public void setPageId(String pageId)
    {
        this.pageId = pageId;
    }

    public String getKeyword1()
    {
        return keyword1;
    }

    public void setKeyword1(String keyword1)
    {
        this.keyword1 = keyword1;
    }

    public String getKeyword2()
    {
        if (keyword2.contains(".0\0")) {
            keyword2 = keyword2.replace(".0", "");
        }
        return keyword2;
    }

    public void setKeyword2(String keyword2)
    {
        this.keyword2 = keyword2;
    }
}
