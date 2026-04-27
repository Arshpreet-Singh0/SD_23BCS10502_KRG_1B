package EST_23BCS10502;

interface SocialMedia{
    public void post();
}

class Whatsapp implements SocialMedia{
    @Override
    public void post() {
        System.out.println("Whatsapp");
    }
}
class Instagram implements SocialMedia{
    @Override
    public void post() {
        System.out.println("Instagram");
    }
}
class Facebook implements SocialMedia{
    @Override
    public void post() {
        System.out.println("Facebook");
    }
}

public class Ques2{

    public static void sharePost(SocialMedia media){
        media.post();
    }

    public static void main(String[] args) {
        Instagram insta = new Instagram();
        sharePost(insta);
    }
}