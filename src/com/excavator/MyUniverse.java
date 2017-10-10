package com.excavator;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;
import no.uia.ats.loader.java3d.max3ds.TLoader3DS;



import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyUniverse extends VirtualUniverse implements KeyListener {

    TransformGroup bottom;
    TransformGroup mainRot;
    TransformGroup arm1Rot;
    TransformGroup arm2Rot;
    TransformGroup hookRot;

    // coordinates of the vehicle in the XY (or in Java XZ) plane
    float posX = 0.0f;
    float posZ = 0.0f;

    // rotation values for each object when the program starts for the first time.
    double mainRotation = 0.0;
    double arm1Angle = Math.PI / 6;
    double arm2Angle = -Math.PI / 3;
    double hookAngle = 0.0;

    // used in test to make object not rotate more than realistically possible
    int arm1Counter = 0;
    int arm2Counter = 0;
    int hookCounter = 0;

    private BoundingSphere bndsphWorld;

    public MyUniverse(Canvas3D aCanvas3D) {
        bndsphWorld = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100000);

        Locale locale = new Locale(this);
        BranchGroup viewBranch = createViewBranch(aCanvas3D);
        locale.addBranchGraph(viewBranch);

        BranchGroup objectBranch = new BranchGroup();
        setBackground(objectBranch);
        setLight(objectBranch);

        Transform3D translate = new Transform3D();
        Transform3D rotate = new Transform3D();


        // Genererer treet med grupper og flytter/roterer objekter til riktig startsted

    /* —————————————————————————————————Hjul/bunnen—————————————————————————————————————— */
        bottom = new TransformGroup();
        bottom.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objectBranch.addChild(bottom);
    /* ————————————————————————————————————Hoveddel—————————————————————————————————————— */
        TransformGroup main = new TransformGroup();
        main.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        bottom.addChild(main);

        mainRot = new TransformGroup();
        mainRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        main.addChild(mainRot);
    /* ————————————————————————————————————————Arm1—————————————————————————————————————— */
        TransformGroup arm1 = new TransformGroup();
        arm1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mainRot.addChild(arm1);

        translate.setTranslation(new Vector3f(0.2f, 0.45f, 0.15f));
        arm1.setTransform(translate);

        arm1Rot = new TransformGroup();
        arm1Rot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        arm1.addChild(arm1Rot);

        rotate.rotZ(arm1Angle);
        arm1Rot.setTransform(rotate);
    /* ————————————————————————————————————————Arm2—————————————————————————————————————— */
        TransformGroup arm2 = new TransformGroup();
        arm2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        arm1Rot.addChild(arm2);

        translate.setTranslation(new Vector3f(0.75f, 0.0f, 0.0f));
        arm2.setTransform(translate);

        arm2Rot = new TransformGroup();
        arm2Rot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        arm2.addChild(arm2Rot);

        rotate.rotZ(arm2Angle);
        arm2Rot.setTransform(rotate);
    /* ——————————————————————————————Den greia på tuppen—————————————————————————————————— */
        TransformGroup hook = new TransformGroup();
        hook.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        arm2Rot.addChild(hook);

        translate.setTranslation(new Vector3f(0.8f, 0.0f, 0.0f));
        hook.setTransform(translate);

        hookRot = new TransformGroup();
        hookRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        hook.addChild(hookRot);


        // importerer 3d-filer til ended av sine respektive rotasjonsgrupper
        objectBranch.addChild(loadObject( "resources/ground.3DS"));
        bottom.addChild(loadObject( "resources/bottom.3DS"));
        mainRot.addChild(loadObject( "resources/main.3DS"));
        arm1Rot.addChild(loadObject( "resources/arm.3DS"));
        arm2Rot.addChild(loadObject( "resources/arm.3DS"));
        hookRot.addChild(loadObject( "resources/hook.3DS"));


        objectBranch.compile();
        locale.addBranchGraph(objectBranch);

        aCanvas3D.addKeyListener(this);
    }

    private TransformGroup loadObject(String aFile) {
        TLoader3DS ld = new TLoader3DS(true);
        TransformGroup tfgModel = ld.LoadModels(aFile);

        Transform3D matDScale = new Transform3D();

        matDScale.setScale(0.01f);
        tfgModel.setTransform(matDScale);
        return tfgModel;
    }

    private void setLight(BranchGroup aObjectGraph) {
        AmbientLight ambientLight = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
        ambientLight.setInfluencingBounds(bndsphWorld);
        aObjectGraph.addChild(ambientLight);

        DirectionalLight dirLight = new DirectionalLight(
                new Color3f(0.8f, 0.8f, 0.8f),
                new Vector3f(0.0f, 0.0f, 1.0f)
        );
        dirLight.setInfluencingBounds(bndsphWorld);
        aObjectGraph.addChild(dirLight);

        dirLight = new DirectionalLight(
                new Color3f(1.0f, 1.0f, 1.0f),
                new Vector3f(-1.0f, -1.0f, -1.0f)
        );
        dirLight.setInfluencingBounds(bndsphWorld);
        aObjectGraph.addChild(dirLight);

    }

    private void setBackground(BranchGroup aObjectBranch) {
        Background bgNode = new Background(new Color3f(1.0f, 1.0f, 1.0f));
        bgNode.setApplicationBounds(bndsphWorld);
        aObjectBranch.addChild(bgNode);
    }

    private BranchGroup createViewBranch(Canvas3D aCanvas3D) {
        BranchGroup bgReturn = new BranchGroup();
        Viewer viewer = new Viewer(aCanvas3D);

        ViewingPlatform viewingPlatform = new ViewingPlatform();
        viewer.setViewingPlatform(viewingPlatform);

        OrbitBehavior orbit = new OrbitBehavior(aCanvas3D, OrbitBehavior.REVERSE_ROTATE);
        orbit.setSchedulingBounds(bndsphWorld);
        viewingPlatform.setViewPlatformBehavior(orbit);
        viewingPlatform.setNominalViewingTransform();

        View view = viewer.getView();
        view.setBackClipDistance(bndsphWorld.getRadius());
        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());

        Transform3D matRotation = new Transform3D();
        matRotation.rotX(Math.toRadians(-10));

        Transform3D matTranslate = new Transform3D();
        matTranslate.set(new Vector3d(0.0, 0.51, 8.0));
        matRotation.mul(matTranslate);

        TransformGroup viewGroup = viewingPlatform.getViewPlatformTransform();
        viewGroup.setTransform(matRotation);

        viewGroup = new TransformGroup();

        viewGroup.addChild(viewingPlatform);
        bgReturn.addChild(viewGroup);

        return bgReturn;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Transform3D translate = new Transform3D();
        Transform3D rotate = new Transform3D();

        // checks which keys are press and also test to make sure the arms and hook doesn't go realistically too far
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            posZ -= 0.05f;
            translate.setTranslation(new Vector3f(posX, 0.0f, posZ));
            bottom.setTransform(translate);
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            posX -= 0.05f;
            translate.setTranslation(new Vector3f(posX, 0.0f, posZ));
            bottom.setTransform(translate);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            posZ += 0.05f;
            translate.setTranslation(new Vector3f(posX, 0.0f, posZ));
            bottom.setTransform(translate);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            posX += 0.05f;
            translate.setTranslation(new Vector3f(posX, 0.0f, posZ));
            bottom.setTransform(translate);
        } else if (e.getKeyCode() == KeyEvent.VK_Q) {
            mainRotation += 0.05;
            rotate.rotY(mainRotation);
            mainRot.setTransform(rotate);
        } else if (e.getKeyCode() == KeyEvent.VK_E) {
            mainRotation -= 0.05;
            rotate.rotY(mainRotation);
            mainRot.setTransform(rotate);
        } else if (e.getKeyCode() == KeyEvent.VK_1 && arm1Counter < 15) {
            arm1Angle += 0.05;
            rotate.rotZ(arm1Angle);
            arm1Rot.setTransform(rotate);
            arm1Counter++;
        } else if (e.getKeyCode() == KeyEvent.VK_2 && arm1Counter > -3) {
            arm1Angle -= 0.05;
            rotate.rotZ(arm1Angle);
            arm1Rot.setTransform(rotate);
            arm1Counter--;
        } else if (e.getKeyCode() == KeyEvent.VK_3 && arm2Counter < 10) {
            arm2Angle += 0.05;
            rotate.rotZ(arm2Angle);
            arm2Rot.setTransform(rotate);
            arm2Counter++;
        } else if (e.getKeyCode() == KeyEvent.VK_4 && arm2Counter > -9) {
            arm2Angle -= 0.05;
            rotate.rotZ(arm2Angle);
            arm2Rot.setTransform(rotate);
            arm2Counter--;
        } else if (e.getKeyCode() == KeyEvent.VK_5 && hookCounter < 18) {
            hookAngle += 0.05;
            rotate.rotZ(hookAngle);
            hookRot.setTransform(rotate);
            hookCounter++;
        } else if (e.getKeyCode() == KeyEvent.VK_6 && hookCounter > -29) {
            hookAngle -= 0.05;
            rotate.rotZ(hookAngle);
            hookRot.setTransform(rotate);
            hookCounter--;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

}