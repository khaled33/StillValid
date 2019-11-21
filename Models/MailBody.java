package com.stillvalid.asus.stillvalid.Models;

public class MailBody {

    public static String getBody(String tocken) {
        String str;

        str = "<html><body><center><img src=''></center>" +
                "<h1 style='color:#3F51B5;text-align:center;'>"+"Créez votre compte StillValid"+"</h1>" +
                "<h3 style='color:#3F51B5;text-align:center;'>Votre compte Still Valid permet d'accéder a votre application Mobile </h3>" +
                "<br><p>" +
                "Votre compte Still Valid a bien été enregistré Vous pouvez dés mantenant accéder vos services. " +
                "pour terminer la création de votre compte vous devez activer a traver" +
                "" +
                " : <br><b>code d'activation : " +tocken+"</b></p> "+
                "<p>Merci ! <br>Equipe StillValide </p><br>" +
                "<p>Ceci est un e-mail généré automatiquement, veuillez ne pas y répondre</p>" +
                "</body></html>";
        return str;
    }
    public static String getBody1(String passe) {
        String str;

        str = "<html><body><center><img src=''></center>" +
                "<h3 style='color:#3F51B5;text-align:center;'>"+"Avez-vous oublié votre mot de passe pour StillValid ?"+"</h3>" +
                "<h3 style='color:#3F51B5;text-align:center;'>Nous sommes là pour vous aider </h3>" +
                "<br><p>Voici le Code de vérification qui vous permettra de vous connecter à votre compte StillValid " +
                ": <br> <br>Code de vérification :<b>" +passe+"</b></p> "+
                "<p>ATTENTION :<br>" +
                "Si vous n'avez pas demandé de rappeler votre mot de passe, IGNOREZ et EFFACEZ ce courriel immédiatement ! Continuez uniquement si vous souhaitez que votre mot de passe soit rappelé.</p><br>" +
                "<p>Merci ! <br>Equipe StillValid </p><br>" +
                "<p>Ceci est un e-mail généré automatiquement, veuillez ne pas y répondre</p>" +
                "</body></html>";
        return str;
    }
}
